package com.example.lunchmate.presentation.profile.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetProfileBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.domain.model.LunchInvitation
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.profile.viewModel.ProfileViewModel
import com.example.lunchmate.presentation.profile.viewModel.ProfileViewModelFactory
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class ProfileBottomSheet(
    val user: User
) : BottomSheetDialogFragment() {

    private lateinit var profileViewModel: ProfileViewModel
    lateinit var binding: BottomSheetProfileBinding
    val calendar = ProfileCalendar(::updateScheduleData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(
            requireActivity(), ProfileViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        )[ProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetProfileBinding.bind(
            inflater.inflate(
                R.layout.bottom_sheet_profile,
                container
            )
        )

        initialiseObservers()
        initialiseUIElements()

        return binding.root
    }

    private fun initialiseUIElements() {
        binding.profileName.text = user.name

        binding.profileNickname.text = user.login

        binding.office.text = user.office.name

        binding.taste.text = user.tastes

        binding.infoText.text = user.aboutMe

        binding.tgButton.setOnClickListener {
            val tgIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://t.me/" + user.messenger)
            )
            startActivity(tgIntent)
        }

        binding.date.text = calendar.getDateStr()
        updateScheduleData()

        binding.rightButton.setOnClickListener{
            binding.leftButton.isEnabled = true
            calendar.increase()
            binding.date.text = calendar.getDateStr()
            if (!calendar.isToday()){
                binding.leftButton.setColorFilter(resources.getColor(R.color.blue_700))
                binding.leftButton.isClickable = true
            }
        }
        binding.leftButton.isEnabled = false
        binding.leftButton.setOnClickListener{
            calendar.decrease()
            binding.date.text = calendar.getDateStr()
            if (calendar.isToday()){
                binding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                binding.leftButton.isClickable = false
            }
        }
    }

    private fun updateScheduleData(){
        profileViewModel.getFreeSlots(user.id, calendar.getCurrentDate(), calendar.getCurrentWeekday())
    }

    private fun initialiseObservers() {
        profileViewModel.scheduleData.observe(viewLifecycleOwner) {
            setUpRV(it)
        }

        profileViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }
    }

    private fun setUpRV(slotsList: ArrayList<Slot>) {
        val slotsAdapter = AvailableSlotsAdapter(slotsList, ::inviteForLunch)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.availableSlots.layoutManager = linearLayoutManager
        binding.availableSlots.adapter = slotsAdapter
        checkEmptyState(slotsList)
    }

    private fun checkEmptyState(slotsList: ArrayList<Slot>) {
        if (slotsList.size == 0) {
            binding.availableSlots.visibility = View.GONE
            binding.emptyText.visibility = View.VISIBLE
        } else {
            binding.availableSlots.visibility = View.VISIBLE
            binding.emptyText.visibility = View.GONE
        }
    }

    override fun getTheme() = R.style.SheetDialog

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.shimmerLayout.visibility = View.GONE
                binding.availableSlots.visibility = View.VISIBLE
            }
            LoadingState.LOADING -> {
                binding.availableSlots.visibility = View.GONE
                binding.shimmerLayout.visibility = View.VISIBLE
            }
            LoadingState.ERROR -> {
                binding.shimmerLayout.visibility = View.GONE
                binding.availableSlots.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun inviteForLunch(timeslotId: Int){
        profileViewModel.inviteForLunch(LunchInvitation("id1", user.id, timeslotId, calendar.getCurrentDate())).observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.shimmerLayout.visibility = View.GONE
                        binding.availableSlots.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Ваше приглашение было отправлено!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    Status.ERROR -> {
                        binding.shimmerLayout.visibility = View.GONE
                        binding.availableSlots.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.availableSlots.visibility = View.GONE
                        binding.shimmerLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}