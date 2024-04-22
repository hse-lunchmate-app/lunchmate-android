package com.example.lunchmate

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunchmate.api.ApiHelper
import com.example.lunchmate.api.RetrofitBuilder
import com.example.lunchmate.databinding.ActivityMainBinding
import com.example.lunchmate.databinding.FragmentAccountEditBinding
import com.example.lunchmate.fragments.NotificationsFragment
import com.example.lunchmate.model.Office
import com.example.lunchmate.model.User
import com.example.lunchmate.repository.MainRepository
import com.example.lunchmate.utils.Status
import com.example.lunchmate.viewModel.MainViewModel
import com.example.lunchmate.viewModel.ViewModelFactory
import com.example.lunchmatelocal.Account
import com.example.lunchmatelocal.AccountFragment
import com.example.lunchmatelocal.HomeFragment
import com.example.lunchmatelocal.ScheduleFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var currentUser: User
    //var sampleAccount: Account = Account(0, "Иван Иванов", 1, "Котлетка с пюрешкой", "Я обычный Иван", "", "ivan12345", "", R.drawable.photo)
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

        val homeFragment = HomeFragment()
        val accountFragment = AccountFragment()
        val scheduleFragment = ScheduleFragment()
        val notificationsFragment = NotificationsFragment()
        setCurrentFragment(homeFragment)

        updateBadge()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.profile->setCurrentFragment(accountFragment)
                R.id.notifications->setCurrentFragment(notificationsFragment)
                R.id.schedule-> setCurrentFragment(scheduleFragment)
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
                backgroundColor = getResources().getColor(R.color.yellow_600)
                badgeTextColor = getResources().getColor(R.color.black)
                maxCharacterCount = 3
            }
        }
    }
}