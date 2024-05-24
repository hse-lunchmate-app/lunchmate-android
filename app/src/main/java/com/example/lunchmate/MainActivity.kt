package com.example.lunchmate

import android.content.Context
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
import com.example.lunchmate.domain.model.City
import com.example.lunchmate.domain.model.Office
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.account.ui.AccountFragment
import com.example.lunchmate.presentation.home.ui.HomeFragment
import com.example.lunchmate.presentation.notifications.ui.NotificationsFragment
import com.example.lunchmate.viewModel.MainViewModel
import com.example.lunchmate.viewModel.ViewModelFactory
import com.example.lunchmatelocal.ScheduleFragment


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

        if (getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).getString("userId", "") == "") {
            with(getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).edit()) {
                putString("userId", "id1")
                putBoolean("darkTheme", false)
                apply()
            }
        } else {
            if (getSharedPreferences("CurrentUserInfo", MODE_PRIVATE).getBoolean(
                    "darkTheme",
                    false
                )
            )
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            if (savedInstanceState == null) {
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
        }
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