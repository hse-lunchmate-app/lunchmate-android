package com.example.lunchmate.presentation.notifications.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentNotificationsBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.model.Lunch
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.notifications.viewModel.NotificationsViewModel
import com.example.lunchmate.presentation.notifications.viewModel.NotificationsViewModelFactory
import com.example.lunchmate.presentation.profile.ui.ProfileBottomSheet
import com.google.android.material.tabs.TabLayout
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var userId: String
    private lateinit var authToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = requireActivity().getSharedPreferences("CurrentUserInfo",AppCompatActivity.MODE_PRIVATE).getString("userId", "")!!
        authToken = requireActivity().getSharedPreferences("CurrentUserInfo",AppCompatActivity.MODE_PRIVATE).getString("authToken", "")!!
        notificationsViewModel = ViewModelProvider(
            requireActivity(), NotificationsViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        )[NotificationsViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationsBinding.bind(view)

        initialiseObservers()
        initialiseUIElements()
    }

    private fun initialiseUIElements() {
        binding.tabMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == binding.tabMenu.getTabAt(1)) {
                    notificationsViewModel.getHistory(authToken, userId)
                } else {
                    notificationsViewModel.getInvitations(authToken, userId)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initialiseObservers() {
        notificationsViewModel.getInvitations(authToken, userId)

        notificationsViewModel.notificationsData.observe(viewLifecycleOwner) {
            setUpRV(it)
        }

        notificationsViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }

        notificationsViewModel.toastMsg.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                notificationsViewModel.toastMsg.postValue(null)
            }
        }
    }

    private fun onProfileClick(user: User) {
        val dialog = ProfileBottomSheet(userId, user, authToken)
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun declineInvitation(lunch: Lunch) {
        notificationsViewModel.declineInvitation(authToken, lunch)
    }

    private fun acceptInvitation(lunch: Lunch) {
        notificationsViewModel.acceptInvitation(authToken, lunch)
    }

    private fun revokeInvitation(lunch: Lunch) {
        notificationsViewModel.revokeInvitation(authToken, lunch)
    }

    private fun setUpRV(notificationsList: ArrayList<Lunch>) {
        val notificationsAdapter = NotificationsAdapter(
            userId,
            notificationsList,
            ::onProfileClick,
            ::declineInvitation,
            ::acceptInvitation,
            ::revokeInvitation
        )
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = notificationsAdapter
        checkEmptyState(notificationsList)
        (activity as MainActivity).updateBadge()
    }

    private fun checkEmptyState(notificationsList: ArrayList<Lunch>) {
        if (notificationsList.size == 0) {
            binding.emptyIcon.visibility = View.VISIBLE
            binding.emptyTitle.visibility = View.VISIBLE
            binding.emptyContent.visibility = View.VISIBLE
        } else {
            binding.emptyIcon.visibility = View.GONE
            binding.emptyTitle.visibility = View.GONE
            binding.emptyContent.visibility = View.GONE
        }
    }

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            LoadingState.LOADING -> {
                binding.recyclerView.visibility = View.INVISIBLE
                binding.shimmerLayout.visibility = View.VISIBLE
            }
            LoadingState.ERROR -> {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
            }
        }
    }
}