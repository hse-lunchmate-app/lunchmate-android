package com.example.lunchmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lunchmatelocal.Account
import com.example.lunchmatelocal.AccountFragment
import com.example.lunchmatelocal.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var currentUser: Account
    val offices = arrayOf("Офис 1", "Офис 2", "Офис 3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val profileFragment = AccountFragment()

        setCurrentFragment(homeFragment)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.profile->setCurrentFragment(profileFragment)
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

}