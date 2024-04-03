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
        binding.photo.setImageResource(activity.currentUser.getPhoto())
        binding.profileName.setText(activity.currentUser.getName())
        binding.profileNickname.setText(activity.currentUser.getLogin())
        binding.telegram.setText(activity.currentUser.getTg())
        binding.office.setText(activity.offices[activity.currentUser.getOffice()])
        binding.taste.setText(activity.currentUser.getTaste())
        binding.infoText.setText(activity.currentUser.getInfo())

        binding.editButton.setOnClickListener {
            setCurrentFragment(accountEditFragment)
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment,fragment)
            commit()
        }
}