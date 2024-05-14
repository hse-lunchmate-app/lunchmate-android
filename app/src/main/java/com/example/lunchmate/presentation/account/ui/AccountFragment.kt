package com.example.lunchmate.presentation.account.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentAccountBinding
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.accountEdit.ui.AccountEditFragment
import com.example.lunchmate.domain.api.Status
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountFragment: Fragment(R.layout.fragment_account) {
    private lateinit var binding: FragmentAccountBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.VISIBLE

        setUpObserver()

        binding.editButton.setOnClickListener {
            setCurrentFragment(AccountEditFragment())
        }

        binding.themeButton.setOnClickListener(View.OnClickListener {
            if(false){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        })
    }

    private fun setUpObserver() {
        val activity = activity as MainActivity
        activity.viewModel.getUser("id1").observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { user ->
                            activity.currentUser = user
                            setUpCurrentUser(activity.currentUser) }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment,fragment)
            commit()
        }

    private fun setUpCurrentUser(currentUser: User){
        //binding.photo.setImageResource(currentUser.getPhoto())
        binding.profileName.text = currentUser.name
        binding.profileNickname.text = currentUser.login
        binding.telegram.text = currentUser.messenger
        binding.office.text = currentUser.office.name
        binding.taste.text = currentUser.tastes
        binding.infoText.text = currentUser.aboutMe
    }
}