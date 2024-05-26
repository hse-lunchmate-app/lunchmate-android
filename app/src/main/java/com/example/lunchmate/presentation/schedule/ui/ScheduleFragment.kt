package com.example.lunchmatelocal

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentScheduleBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.model.SlotPatch
import com.example.lunchmate.domain.model.SlotPost
import com.example.lunchmate.presentation.schedule.ui.*
import com.example.lunchmate.presentation.schedule.viewModel.ScheduleViewModel
import com.example.lunchmate.presentation.schedule.viewModel.ScheduleViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleFragment : Fragment(R.layout.fragment_schedule) {
    private val weekdays = arrayOf(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY
    )
    private val monthNames = arrayOf(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь"
    )
    private lateinit var binding: FragmentScheduleBinding
    private var currentDay: CurrentDay? = null
    private lateinit var slotsAdapter: SlotsAdapter
    private val calendar: ScheduleCalendar = ScheduleCalendar()
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var weekdayButtons: List<CurrentDay>
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = requireActivity().getSharedPreferences(
            "CurrentUserInfo",
            AppCompatActivity.MODE_PRIVATE
        ).getString("userId", "")!!
        scheduleViewModel = ViewModelProvider(
            requireActivity(), ScheduleViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        )[ScheduleViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.bind(view)
        initialiseObservers()
        initialiseUIElements()
    }

    private fun initialiseObservers() {
        scheduleViewModel.scheduleData.observe(viewLifecycleOwner) {
            setUpRV(it)
        }

        scheduleViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }

        scheduleViewModel.indicatorsData.observe(viewLifecycleOwner) {
            updateIndicators(it)
        }
    }

    private fun initialiseUIElements() {
        weekdayButtons = listOf<CurrentDay>(
            CurrentDay(
                1, binding.mondayBtn, binding.monday,
                binding.mondayDate, binding.mondaySlotsIndicator
            ), CurrentDay(
                2, binding.tuesdayBtn, binding.tuesday,
                binding.tuesdayDate, binding.tuesdaySlotsIndicator
            ), CurrentDay(
                3, binding.wednesdayBtn, binding.wednesday,
                binding.wednesdayDate, binding.wednesdaySlotsIndicator
            ), CurrentDay(
                4, binding.thursdayBtn, binding.thursday,
                binding.thursdayDate, binding.thursdaySlotsIndicator
            ), CurrentDay(
                5, binding.fridayBtn, binding.friday,
                binding.fridayDate, binding.fridaySlotsIndicator
            )
        )

        currentDay = setUpCurrentWeek()

        for (i in weekdayButtons.indices) {
            weekdayButtons[i].btn.setOnClickListener {
                onWeekdayClick(i)
            }
        }

        binding.addSlotBtn.setOnClickListener {
            openAddSlot()
        }

        binding.rightButton.setOnClickListener {
            binding.leftButton.isEnabled = true
            setUpWeek(++calendar.weekNum)
            if (!calendar.isCurrentWeek()) {
                binding.leftButton.setColorFilter(resources.getColor(R.color.black))
                binding.leftButton.isClickable = true
            }
        }
        binding.leftButton.isEnabled = false
        binding.leftButton.setOnClickListener {
            calendar.weekNum--
            if (calendar.isCurrentWeek()) {
                binding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                binding.leftButton.isClickable = false
                setUpCurrentWeek()
            } else {
                setUpWeek(calendar.weekNum)
            }
        }
    }

    private fun updateIndicators(indicators: List<Boolean>) {
        for (i in indicators.indices) {
            if (indicators[i])
                weekdayButtons[i].slotsIndicator.visibility = View.VISIBLE
            else
                weekdayButtons[i].slotsIndicator.visibility = View.GONE
        }
    }

    private fun setUpRV(slotsList: ArrayList<Slot>) {
        slotsAdapter = SlotsAdapter(::openFreeSlot, ::openReservedSlot, slotsList)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = slotsAdapter
        checkSlotsCount(slotsList)
    }

    private fun openAddSlot() {
        val dialog = AddSlotBottomSheet(
            userId,
            calendar.getDateStr(),
            calendar.getCurrentDate(),
            ::addSlot
        )
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun openFreeSlot(slot: Slot) {
        val dialog = FreeSlotBottomSheet(
            calendar.getDateStr(),
            calendar.getCurrentDate(),
            slot,
            ::updateSlot,
            ::deleteSlot
        )
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun openReservedSlot(slot: Slot) {
        val dialog = ReservedSlotBottomSheet(
            userId,
            calendar.getDateStr(),
            slot,
            ::cancelReservation
        )
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun cancelReservation(slot: Slot) {
        scheduleViewModel.cancelReservation(
            slot, userId, calendar.getWeekStart(), calendar.getWeekFinish()
        )
    }

    private fun addSlot(slotPost: SlotPost) {
        scheduleViewModel.postSlot(slotPost)
    }

    private fun deleteSlot(slot: Slot) {
        scheduleViewModel.deleteSlot(slot)
    }

    private fun updateSlot(slot: Slot, slotPatch: SlotPatch) {
        scheduleViewModel.patchSlot(slot, slotPatch)
    }

    private fun onWeekdayClick(i: Int) {
        calendar.setCurrentDay(weekdays[i])

        if (currentDay != null && weekdayButtons[i].btn != currentDay!!.btn) {
            unselectButton(currentDay!!)
        }

        selectButton(weekdayButtons[i])
        scheduleViewModel.getAllSlots(
            userId,
            calendar.getCurrentDate(),
            currentDay!!.weekday
        )
    }

    private fun updateIndicators() {
        scheduleViewModel.getLunchIndicators(
            userId,
            calendar.getWeekStart(),
            calendar.getWeekFinish()
        )
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setUpCurrentWeek(): CurrentDay {
        calendar.setToday()
        updateIndicators()
        val setUpCalendar = Calendar.getInstance()

        val dates = arrayOf(
            binding.mondayDate,
            binding.tuesdayDate,
            binding.wednesdayDate,
            binding.thursdayDate,
            binding.fridayDate,
            binding.saturdayDate,
            binding.sundayDate
        )
        val months: ArrayList<Int> = ArrayList<Int>()
        for (i in weekdays.indices) {
            setUpCalendar.set(Calendar.DAY_OF_WEEK, weekdays[i])
            dates[i].text = SimpleDateFormat("dd").format(setUpCalendar.time)
            months.add(setUpCalendar.get(Calendar.MONTH))
        }

        if (months[0] != months[6]) {
            binding.month.text = monthNames[months[0]] + " - " + monthNames[months[6]]
        } else {
            binding.month.text = monthNames[months[0]]
        }

        for (i in weekdayButtons.indices) {
            if (calendar.dayNum == weekdays[i]) {
                onWeekdayClick(i)
                return weekdayButtons[i]
            }
        }

        onWeekdayClick(4)
        return weekdayButtons[4]
    }

    private fun unselectButton(day: CurrentDay) {
        day.btn.setBackgroundResource(R.drawable.rounded_blue_stroke)
        day.text.setTextColor(resources.getColor(R.color.blue_700))
        day.dateView.setTextColor(resources.getColor(R.color.blue_700))
        day.slotsIndicator.setColorFilter(resources.getColor(R.color.blue_700))
    }

    private fun selectButton(day: CurrentDay) {
        currentDay = day
        day.btn.setBackgroundResource(R.drawable.rounded_yellow)
        day.text.setTextColor(Color.parseColor("#FF000000"))
        day.dateView.setTextColor(Color.parseColor("#FF000000"))
        day.slotsIndicator.setColorFilter(Color.parseColor("#FF000000"))
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setUpWeek(week_num: Int) {
        calendar.setCurrentDay(Calendar.MONDAY)
        updateIndicators()
        val setUpCalendar = Calendar.getInstance()
        setUpCalendar.set(Calendar.WEEK_OF_YEAR, week_num)

        val dates = arrayOf(
            binding.mondayDate,
            binding.tuesdayDate,
            binding.wednesdayDate,
            binding.thursdayDate,
            binding.fridayDate,
            binding.saturdayDate,
            binding.sundayDate
        )
        val months: ArrayList<Int> = ArrayList<Int>()
        for (i in weekdays.indices) {
            setUpCalendar.set(Calendar.DAY_OF_WEEK, weekdays[i])
            dates[i].text = SimpleDateFormat("dd").format(setUpCalendar.time)
            months.add(setUpCalendar.get(Calendar.MONTH))
            if (i < 5)
                unselectButton(weekdayButtons[i])
        }

        if (months[0] != months[6]) {
            binding.month.text = monthNames[months[0]] + " - " + monthNames[months[6]]
        } else {
            binding.month.text = monthNames[months[0]]
        }

        onWeekdayClick(0)
    }

    private fun checkSlotsCount(slotsList: ArrayList<Slot>) {
        if (slotsList.size >= 2) {
            binding.addSlotBtn.visibility = View.GONE
        } else {
            binding.addSlotBtn.visibility = View.VISIBLE
        }
        checkEmptyState(slotsList)
    }

    private fun checkEmptyState(slotsList: ArrayList<Slot>) {
        if (slotsList.size == 0) {
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
                binding.recyclerView.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_LONG).show()
            }
        }
    }
}