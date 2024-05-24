package com.example.lunchmate.presentation.notifications.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentNotificationsBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.model.Lunch
import com.example.lunchmate.domain.model.Notification
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.notifications.viewModel.NotificationsViewModel
import com.example.lunchmate.presentation.notifications.viewModel.NotificationsViewModelFactory
import com.example.lunchmate.presentation.profile.ui.ProfileBottomSheet
import com.example.lunchmate.presentation.schedule.viewModel.ScheduleViewModel
import com.example.lunchmate.presentation.schedule.viewModel.ScheduleViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    notificationsViewModel.getHistory("id1")
                } else {
                    notificationsViewModel.getInvitations("id1")
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initialiseObservers() {
        notificationsViewModel.getInvitations("id1")

        notificationsViewModel.notificationsData.observe(viewLifecycleOwner) {
            setUpRV(it)
        }

        notificationsViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }

        notificationsViewModel.toastMsg.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun onProfileClick(user: User) {
        val dialog = ProfileBottomSheet(user)
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun declineInvitation(lunch: Lunch) {
        notificationsViewModel.declineInvitation(lunch)
    }

    private fun acceptInvitation(lunch: Lunch) {
        notificationsViewModel.acceptInvitation(lunch)
    }

    private fun revokeInvitation(lunch: Lunch) {
        notificationsViewModel.revokeInvitation(lunch)
    }

    private fun setUpRV(notificationsList: ArrayList<Lunch>) {
        val notificationsAdapter = NotificationsAdapter(
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
        binding.recyclerView.itemAnimator = SlideInLeftAnimator()
        checkEmptyState(notificationsList)
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
                Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_LONG).show()
            }
        }
    }
}