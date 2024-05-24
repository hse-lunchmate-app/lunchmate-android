package com.example.lunchmate

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.databinding.ActivityMainBinding
import com.example.lunchmate.presentation.notifications.ui.NotificationsFragment
import com.example.lunchmate.domain.model.Office
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.domain.model.City
import com.example.lunchmate.viewModel.MainViewModel
import com.example.lunchmate.viewModel.ViewModelFactory
import com.example.lunchmate.presentation.account.ui.AccountFragment
import com.example.lunchmate.presentation.home.ui.HomeFragment
import com.example.lunchmatelocal.ScheduleFragment


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    var currentUser: User = User("id1", "v.utkin", "Ваня Ваня", "t.me/testing", "Тестинг", "Тестинг", Office(1, "Tinkoff Space", City(1, "Москва")))
    lateinit var offices: List<Office>
    var officeNames: ArrayList<String> = ArrayList<String>()
    var badge_counter = 13
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        try{
            setContentView(binding.root)
            viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
            )[MainViewModel::class.java]
        }
        catch(e: Exception){
            Log.d("TAG", "Error")
        }

        if (savedInstanceState == null) {
            setCurrentFragment(HomeFragment())
        }
        updateBadge()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(HomeFragment())
                R.id.profile->setCurrentFragment(AccountFragment())
                R.id.notifications->setCurrentFragment(NotificationsFragment())
                R.id.schedule-> setCurrentFragment(ScheduleFragment())
            }
            true
        }

        viewModel.getOffices().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { officeList ->
                            offices = officeList
                            for (office in offices)
                                officeNames.add(office.name)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment,fragment)
            commit()
        }

    fun updateBadge(){
        if (badge_counter == 0){
            binding.bottomNavigationView.getBadge(R.id.notifications)?.isVisible = false
            binding.bottomNavigationView.removeBadge(R.id.notifications)
        }
        else {
            binding.bottomNavigationView.getOrCreateBadge(R.id.notifications).apply{
                isVisible = true
                number = badge_counter
                backgroundColor = resources.getColor(R.color.yellow_600)
                badgeTextColor = Color.parseColor("#FF000000")
                maxCharacterCount = 3
            }
        }
    }
}