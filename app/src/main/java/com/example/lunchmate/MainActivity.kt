package com.example.lunchmate

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
import com.example.lunchmate.viewModel.MainViewModel
import com.example.lunchmate.viewModel.ViewModelFactory
import com.example.lunchmate.presentation.account.ui.AccountFragment
import com.example.lunchmate.presentation.home.ui.HomeFragment
import com.example.lunchmatelocal.ScheduleFragment


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