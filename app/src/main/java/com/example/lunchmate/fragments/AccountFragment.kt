package com.example.lunchmatelocal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentAccountBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountFragment: Fragment(R.layout.fragment_account) {
    private lateinit var binding: FragmentAccountBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        val accountEditFragment = AccountEditFragment()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setVisibility(View.VISIBLE)

        val activity = activity as MainActivity
        setUpCurrentUser(activity.currentUser, activity.offices)

        binding.editButton.setOnClickListener {
            setCurrentFragment(accountEditFragment)
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment,fragment)
            commit()
        }

    private fun setUpCurrentUser(currentUser: Account, offices: Array<String>){
        binding.photo.setImageResource(currentUser.getPhoto())
        binding.profileName.setText(currentUser.getName())
        binding.profileNickname.setText(currentUser.getLogin())
        binding.telegram.setText(currentUser.getTg())
        binding.office.setText(offices[currentUser.getOffice()])
        binding.taste.setText(currentUser.getTaste())
        binding.infoText.setText(currentUser.getInfo())
    }
}