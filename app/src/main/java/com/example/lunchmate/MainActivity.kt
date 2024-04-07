package com.example.lunchmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lunchmate.databinding.ActivityMainBinding
import com.example.lunchmate.databinding.FragmentAccountEditBinding
import com.example.lunchmate.fragments.NotificationsFragment
import com.example.lunchmatelocal.Account
import com.example.lunchmatelocal.AccountFragment
import com.example.lunchmatelocal.HomeFragment
import com.example.lunchmatelocal.ScheduleFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var currentUser: Account
    val offices = arrayOf("Офис 1", "Офис 2", "Офис 3")
    var badge_counter = 13
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        currentUser = Account("Иван Иванов", 0,"Котлетка с пюрешкой", "Я обычный Иван", "ivan123", "ivan12345", "0000", R.drawable.photo)
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