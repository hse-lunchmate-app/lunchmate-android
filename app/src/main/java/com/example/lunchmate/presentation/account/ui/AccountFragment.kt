package com.example.lunchmate.presentation.account.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentAccountBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.accountEdit.ui.AccountEditFragment
import com.example.lunchmate.presentation.account.viewModel.AccountViewModel
import com.example.lunchmate.presentation.account.viewModel.AccountViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountFragment : Fragment(R.layout.fragment_account) {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = requireActivity().getSharedPreferences("CurrentUserInfo",AppCompatActivity.MODE_PRIVATE).getString("userId", "")!!
        accountViewModel = ViewModelProvider(
            requireActivity(), AccountViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        )[AccountViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)

        initialiseObservers()
        initialiseUIElements()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initialiseUIElements() {
        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.VISIBLE

        binding.editButton.setOnClickListener {
            setCurrentFragment(AccountEditFragment())
        }

        binding.themeButton.setOnClickListener(View.OnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                val sharedPref = requireActivity().getSharedPreferences(
                    "CurrentUserInfo",
                    AppCompatActivity.MODE_PRIVATE
                )
                with(sharedPref.edit()) {
                    putBoolean("darkTheme", false)
                    apply()
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                val sharedPref = requireActivity().getSharedPreferences(
                    "CurrentUserInfo",
                    AppCompatActivity.MODE_PRIVATE
                )
                with(sharedPref.edit()) {
                    putBoolean("darkTheme", true)
                    apply()
                }
            }
        })
    }

    private fun initialiseObservers() {
        accountViewModel.getUser(userId)

        accountViewModel.accountData.observe(viewLifecycleOwner) {
            setUpCurrentUser(it)
        }

        accountViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, fragment)
            commit()
        }

    private fun setUpCurrentUser(currentUser: User) {
        binding.profileName.text = currentUser.name
        binding.profileNickname.text = currentUser.login
        binding.telegram.text = currentUser.messenger
        binding.office.text = currentUser.office.name
        binding.taste.text = currentUser.tastes
        binding.infoText.text = currentUser.aboutMe
    }

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.shimmerPhoto.visibility = View.GONE
                binding.photo.visibility = View.VISIBLE
            }
            LoadingState.LOADING -> {
                binding.shimmerPhoto.visibility = View.VISIBLE
                binding.photo.visibility = View.INVISIBLE
            }
            LoadingState.ERROR -> {
                binding.shimmerPhoto.visibility = View.GONE
                binding.photo.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_LONG).show()
            }
        }
    }
}