package com.example.lunchmate.presentation.schedule.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetReservedSlotBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.domain.model.LunchInvitation
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.profile.ui.AvailableSlotsAdapter
import com.example.lunchmate.presentation.profile.ui.ProfileCalendar
import com.example.lunchmate.presentation.profile.viewModel.ProfileViewModel
import com.example.lunchmate.presentation.profile.viewModel.ProfileViewModelFactory
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReservedSlotBottomSheet(
    private val currentUserId: String,
    val date: String,
    val slot: Slot,
    val cancelReservation: (Slot) -> Unit
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetReservedSlotBinding
    private lateinit var profileViewModel: ProfileViewModel
    private val calendar = ProfileCalendar(::updateScheduleData)

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
        binding = BottomSheetReservedSlotBinding.bind(
            inflater.inflate(
                R.layout.bottom_sheet_reserved_slot,
                container
            )
        )

        initialiseObservers()
        initialiseUIElements()

        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initialiseUIElements() {
        binding.date.text = date

        binding.start.text = slot.data.startTime.substring(0, 5)

        binding.finish.text = slot.data.endTime.substring(0, 5)

        binding.lunchMate.text = slot.lunchMate?.name

        binding.profileNickname.text = slot.lunchMate?.login

        binding.office.text = slot.lunchMate?.office?.name

        binding.taste.text = slot.lunchMate?.tastes

        binding.infoText.text = slot.lunchMate?.aboutMe

        binding.tgButton.setOnClickListener {
            val tgIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://t.me/" + slot.lunchMate?.messenger)
            )
            startActivity(tgIntent)
        }


        binding.dateSchedule.text = calendar.getDateStr()
        updateScheduleData()

        binding.rightButton.setOnClickListener {
            binding.leftButton.isEnabled = true
            calendar.increase()
            binding.dateSchedule.text = calendar.getDateStr()
            if (!calendar.isToday()) {
                binding.leftButton.setColorFilter(resources.getColor(R.color.blue_700))
                binding.leftButton.isClickable = true
            }
        }
        binding.leftButton.isEnabled = false
        binding.leftButton.setOnClickListener {
            calendar.decrease()
            binding.dateSchedule.text = calendar.getDateStr()
            if (calendar.isToday()) {
                binding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                binding.leftButton.isClickable = false
            }
        }

        binding.lunchMate.setOnClickListener {
            if (binding.scrollView.isVisible) {
                binding.lunchMate.icon = resources.getDrawable(R.drawable.arrow_right)
                binding.scrollView.visibility = View.GONE
            } else {
                binding.lunchMate.icon = resources.getDrawable(R.drawable.arrow_down)
                binding.scrollView.visibility = View.VISIBLE
            }
        }

        if (slot.data.permanent) {
            binding.repeatingIndicator.visibility = View.VISIBLE
        }

    }

    private fun initialiseObservers() {
        profileViewModel.scheduleData.observe(viewLifecycleOwner) {
            setUpRV(it)
        }

        profileViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }

        profileViewModel.toastMsg.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                profileViewModel.toastMsg.postValue(null)
            }
        }
    }

    private fun setUpRV(slotsList: java.util.ArrayList<Slot>) {
        val slotsAdapter = AvailableSlotsAdapter(slotsList, ::inviteForLunch)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.availableSlots.layoutManager = linearLayoutManager
        binding.availableSlots.adapter = slotsAdapter
        checkEmptyState(slotsList)
    }

    private fun checkEmptyState(slotsList: java.util.ArrayList<Slot>) {
        if (slotsList.size == 0) {
            binding.availableSlots.visibility = View.GONE
            binding.emptyText.visibility = View.VISIBLE
        } else {
            binding.availableSlots.visibility = View.VISIBLE
            binding.emptyText.visibility = View.GONE
        }
    }

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.availableSlots.visibility = View.VISIBLE
                binding.shimmerLayout.visibility = View.GONE
            }
            LoadingState.LOADING -> {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.availableSlots.visibility = View.INVISIBLE
            }
            LoadingState.ERROR -> {
                binding.shimmerLayout.visibility = View.INVISIBLE
                binding.availableSlots.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun inviteForLunch(timeslotId: Int) {
        profileViewModel.inviteForLunch(
            LunchInvitation(
                currentUserId,
                slot.lunchMate!!.id,
                timeslotId,
                calendar.getCurrentDate()
            )
        )
    }

    private fun updateScheduleData() {
        profileViewModel.getFreeSlots(
            slot.lunchMate!!.id,
            calendar.getCurrentDate(),
            calendar.getCurrentWeekday()
        )
    }

    override fun getTheme() = R.style.SheetDialog

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            val coordinator = (it as BottomSheetDialog)
                .findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            val containerLayout =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.container)
            val buttons =
                bottomSheetDialog.layoutInflater.inflate(R.layout.item_cancel_button, null)

            val cancelBtn = buttons.findViewById<AppCompatButton>(R.id.cancelBtn)
            cancelBtn.setOnClickListener {
                cancelReservation(slot)
                bottomSheetDialog.dismiss()
            }

            buttons.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
            containerLayout!!.addView(buttons)

            buttons.post {
                (coordinator!!.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    buttons.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    this.bottomMargin = buttons.measuredHeight
                    containerLayout.requestLayout()
                }
            }
        }

        return bottomSheetDialog
    }
}