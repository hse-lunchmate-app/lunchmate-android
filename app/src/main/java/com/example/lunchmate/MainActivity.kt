package com.example.lunchmate

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunchmate.databinding.ActivityMainBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.presentation.account.ui.AccountFragment
import com.example.lunchmate.presentation.home.ui.HomeFragment
import com.example.lunchmate.presentation.notifications.ui.NotificationsFragment
import com.example.lunchmate.viewModel.MainViewModel
import com.example.lunchmate.viewModel.ViewModelFactory
import com.example.lunchmatelocal.ScheduleFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_LunchMate)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        try {
            setContentView(binding.root)
            viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
            )[MainViewModel::class.java]
        } catch (e: Exception) {
            Log.d("TAG", "Error")
        }

        if (getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).getString(
                "authToken",
                ""
            ) == "" || getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).getString(
                "userId",
                ""
            ) == ""
        ) {
            setUpLoginScreen()
        } else {
            setUpHomeScreen(savedInstanceState)
        }
    }

    fun setUpLoginScreen() {
        with(getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).edit()) {
            putString("authToken", "")
            putString("userId", "")
            apply()
        }

        binding.edittextLogin.setText("")
        binding.edittextPassword.setText("")
        binding.bottomNavigationView.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
        binding.loginBtn.setOnClickListener {
            if (emptyFieldsCheck()) {
                clearErrors()
                val encodedString = encode(
                    binding.edittextLogin.text.toString(),
                    binding.edittextPassword.text.toString()
                )

                with(getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).edit()) {
                    putString("authToken", "Basic $encodedString")
                    apply()
                }

                viewModel.getUserId(
                    getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).getString(
                        "authToken",
                        ""
                    )!!
                ).observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { userId ->
                                    with(
                                        getSharedPreferences(
                                            "CurrentUserInfo",
                                            MODE_PRIVATE
                                        ).edit()
                                    ) {
                                        putString("userId", userId.id)
                                        apply()
                                    }
                                    setUpHomeScreen(null)
                                }
                            }
                            Status.ERROR -> {
                                if (it.message == "HTTP 403 Forbidden")
                                    binding.wrongLoginOrPassword.visibility = View.VISIBLE
                                else
                                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
            }
        }
    }

    private fun encode(login: String, password: String): String {
        val originalString = login.trim() + ":" + password.trim()
        return Base64.getEncoder().encodeToString(originalString.toByteArray())
    }

    private fun clearErrors() {
        binding.edittextLogin.setBackgroundResource(R.drawable.rounded_et)
        binding.errorMsgLogin.visibility = View.GONE
        binding.labelLogin.setTextColor(resources.getColor(R.color.label))
        binding.edittextPassword.setBackgroundResource(R.drawable.rounded_et)
        binding.errorMsgPassword.visibility = View.GONE
        binding.labelPassword.setTextColor(resources.getColor(R.color.label))
        binding.wrongLoginOrPassword.visibility = View.GONE
    }

    private fun emptyFieldsCheck(): Boolean {
        var flag = true
        if (binding.edittextLogin.text.toString().trim().isEmpty()) {
            binding.edittextLogin.setBackgroundResource(R.drawable.rounded_et_error)
            binding.errorMsgLogin.visibility = View.VISIBLE
            binding.labelLogin.setTextColor(resources.getColor(R.color.red_700))
            flag = false
        }
        if (binding.edittextPassword.text.toString().trim().isEmpty()) {
            binding.edittextPassword.setBackgroundResource(R.drawable.rounded_et_error)
            binding.errorMsgPassword.visibility = View.VISIBLE
            binding.labelPassword.setTextColor(resources.getColor(R.color.red_700))
            flag = false
        }
        return flag
    }

    private fun setUpHomeScreen(savedInstanceState: Bundle?) {
        setTheme()

        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.home
            setCurrentFragment(HomeFragment())
        }

        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(HomeFragment())
                R.id.profile -> setCurrentFragment(AccountFragment())
                R.id.notifications -> setCurrentFragment(NotificationsFragment())
                R.id.schedule -> setCurrentFragment(ScheduleFragment())
            }
            true
        }
        updateBadge()

        binding.loginLayout.visibility = View.GONE
    }

    private fun setTheme() {
        if (getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).getBoolean(
                "darkTheme",
                false
            )
        )
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, fragment)
            commit()
        }

    fun updateBadge() {
        viewModel.getInvitationsCount(
            getSharedPreferences(
                "CurrentUserInfo",
                MODE_PRIVATE
            ).getString("authToken", "")!!,
            getSharedPreferences(
                "CurrentUserInfo",
                MODE_PRIVATE
            ).getString("userId", "")!!
        ).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { count ->
                            if (count == 0) {
                                binding.bottomNavigationView.getBadge(R.id.notifications)?.isVisible =
                                    false
                                binding.bottomNavigationView.removeBadge(R.id.notifications)
                            } else {
                                binding.bottomNavigationView.getOrCreateBadge(R.id.notifications)
                                    .apply {
                                        isVisible = true
                                        number = count
                                        backgroundColor = resources.getColor(R.color.yellow_600)
                                        badgeTextColor = Color.parseColor("#FF000000")
                                        maxCharacterCount = 3
                                    }
                            }
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {}
                }
            }
        }
    }
}