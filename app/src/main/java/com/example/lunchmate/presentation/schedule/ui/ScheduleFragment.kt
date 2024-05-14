package com.example.lunchmatelocal

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentScheduleBinding
import com.example.lunchmate.presentation.schedule.ui.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class CurrentDay(
    var btn: LinearLayout,
    var text: TextView,
    var date: TextView,
    var slotsIndicator: ImageView,
)

class ScheduleFragment: Fragment(R.layout.fragment_schedule) {
    private val weekdays = arrayOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)
    private val monthNames = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    private lateinit var binding: FragmentScheduleBinding
    private var currentDay: CurrentDay? = null
    private lateinit var slotsAdapter: SlotsAdapter
    var slotsList: ArrayList<Slot> = ArrayList<Slot>()
    private val calendar: ScheduleCalendar = ScheduleCalendar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.bind(view)

        slotsList.add(Slot(0, "1 марта","11:00", "12:00", true))
        setUpRV(slotsList)

        currentDay = setUpCurrentWeek()
        if (currentDay != null){
            currentDay!!.btn.setBackgroundResource(R.drawable.rounded_yellow)
            currentDay!!.text.setTextColor(resources.getColor(R.color.black))
            currentDay!!.date.setTextColor(resources.getColor(R.color.black))
            currentDay!!.slotsIndicator.setColorFilter(resources.getColor(R.color.black))
        }
        binding.mondayBtn.setOnClickListener {
            calendar.setCurrentDay(Calendar.MONDAY)
            onWeekdayClick(CurrentDay(
                binding.mondayBtn, binding.monday,
                binding.mondayDate, binding.mondaySlotsIndicator
            ))
        }
        binding.tuesdayBtn.setOnClickListener {
            calendar.setCurrentDay(Calendar.TUESDAY)
            onWeekdayClick(CurrentDay(
                binding.tuesdayBtn, binding.tuesday,
                binding.tuesdayDate, binding.tuesdaySlotsIndicator
            ))
        }
        binding.wednesdayBtn.setOnClickListener {
            calendar.setCurrentDay(Calendar.WEDNESDAY)
            onWeekdayClick(CurrentDay(
                binding.wednesdayBtn, binding.wednesday,
                binding.wednesdayDate, binding.wednesdaySlotsIndicator
            ))
        }
        binding.thursdayBtn.setOnClickListener {
            calendar.setCurrentDay(Calendar.THURSDAY)
            onWeekdayClick(CurrentDay(
                binding.thursdayBtn, binding.thursday,
                binding.thursdayDate, binding.thursdaySlotsIndicator
            ))
        }
        binding.fridayBtn.setOnClickListener {
            calendar.setCurrentDay(Calendar.FRIDAY)
            onWeekdayClick(CurrentDay(
                binding.fridayBtn, binding.friday,
                binding.fridayDate, binding.fridaySlotsIndicator
            ))
        }
        binding.addSlotBtn.setOnClickListener {
            openAddSlot()
        }
        binding.rightButton.setOnClickListener{
            binding.leftButton.isEnabled = true
            setUpWeek(++calendar.week_num)
            if (!calendar.isCurrentWeek()){
                binding.leftButton.setColorFilter(resources.getColor(R.color.black))
                binding.leftButton.isClickable = true
            }
        }
        binding.leftButton.isEnabled = false
        binding.leftButton.setOnClickListener{
            setUpWeek(--calendar.week_num)
            if (calendar.isCurrentWeek()){
                binding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                binding.leftButton.isClickable = false
            }
        }
    }

    private fun setUpRV(slotsList: ArrayList<Slot>){
        slotsAdapter = SlotsAdapter(::openFreeSlot, ::openReservedSlot, slotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.slotsRV.layoutManager = linearLayoutManager
        binding.slotsRV.adapter = slotsAdapter
        checkSlotsCount()
    }

    private fun openAddSlot(){
        val dialog = AddSlotBottomSheet(calendar.getDateStr(), currentDay!!.date.toString(), binding.month.toString(), ::addSlot)
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun openFreeSlot(position: Int){
        val dialog = FreeSlotBottomSheet(calendar.getDateStr(), slotsList, position, ::updateSlot, ::deleteSlot)
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun openReservedSlot(position: Int){
        val dialog = ReservedSlotBottomSheet(calendar.getDateStr(), ::cancelReservation, slotsList, position)
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun cancelReservation(position: Int){
        slotsList[position].setLunchMate(null)
        slotsAdapter.notifyItemChanged(position)
    }

    private fun addSlot(newSlot: Slot){
        slotsList.add(newSlot)
        slotsAdapter.notifyItemInserted(slotsList.size - 1)
        checkSlotsCount()
    }

    private fun deleteSlot(position: Int){
        slotsList.removeAt(position)
        slotsAdapter.notifyDataSetChanged()
        checkSlotsCount()
    }

    private fun updateSlot(position: Int, start: String, finish: String, isRepeating: Boolean){
        slotsList[position].setStart(start)
        slotsList[position].setFinish(finish)
        slotsList[position].setIsRepeating(isRepeating)
        slotsAdapter.notifyItemChanged(position)
    }

    private fun onWeekdayClick(newDay: CurrentDay) {
        if (currentDay != null && newDay.btn != currentDay!!.btn) {
                currentDay!!.btn.setBackgroundResource(R.drawable.rounded_blue_stroke)
                currentDay!!.text.setTextColor(resources.getColor(R.color.blue_700))
                currentDay!!.date.setTextColor(resources.getColor(R.color.blue_700))
                currentDay!!.slotsIndicator.setColorFilter(resources.getColor(R.color.blue_700))
        }
        currentDay = CurrentDay(newDay.btn, newDay.text, newDay.date, newDay.slotsIndicator)
        newDay.btn.setBackgroundResource(R.drawable.rounded_yellow)
        newDay.text.setTextColor(resources.getColor(R.color.black))
        newDay.date.setTextColor(resources.getColor(R.color.black))
        newDay.slotsIndicator.setColorFilter(resources.getColor(R.color.black))
    }

    private fun setUpCurrentWeek(): CurrentDay? {
        val setUpCalendar = Calendar.getInstance()

        val dates = arrayOf(binding.mondayDate, binding.tuesdayDate, binding.wednesdayDate, binding.thursdayDate, binding.fridayDate, binding.saturdayDate, binding.sundayDate)
        val months: ArrayList<Int> = ArrayList<Int>()
        for (i in weekdays.indices){
            setUpCalendar.set(Calendar.DAY_OF_WEEK, weekdays[i])
            dates[i].text = SimpleDateFormat("dd").format(setUpCalendar.time)
            months.add(setUpCalendar.get(Calendar.MONTH))
        }

        if (months[0] != months[6]){
            binding.month.text = monthNames[months[0]]+" - "+monthNames[months[6]]
        }
        else{
            binding.month.text = monthNames[months[0]]
        }

        when (calendar.day_num) {
            Calendar.MONDAY -> {
                return CurrentDay(
                binding.mondayBtn, binding.monday,
                binding.mondayDate, binding.mondaySlotsIndicator
            )}
            Calendar.TUESDAY -> {
                return CurrentDay(
                    binding.tuesdayBtn, binding.tuesday,
                    binding.tuesdayDate, binding.tuesdaySlotsIndicator
                )}
            Calendar.WEDNESDAY -> {
                return CurrentDay(
                    binding.wednesdayBtn, binding.wednesday,
                    binding.wednesdayDate, binding.wednesdaySlotsIndicator
                )}
            Calendar.THURSDAY -> {
                return CurrentDay(
                    binding.thursdayBtn, binding.thursday,
                    binding.thursdayDate, binding.thursdaySlotsIndicator
                )}
            Calendar.FRIDAY -> {
                return CurrentDay(
                    binding.fridayBtn, binding.friday,
                    binding.fridayDate, binding.fridaySlotsIndicator
                )}
        }
        return null
    }

    private fun setUpWeek(week_num: Int) {
        calendar.setCurrentDay(Calendar.MONDAY)
        val setUpCalendar = Calendar.getInstance()
        setUpCalendar.set(Calendar.WEEK_OF_YEAR, week_num)

        val dates = arrayOf(binding.mondayDate, binding.tuesdayDate, binding.wednesdayDate, binding.thursdayDate, binding.fridayDate, binding.saturdayDate, binding.sundayDate)
        val months: ArrayList<Int> = ArrayList<Int>()
        for (i in weekdays.indices){
            setUpCalendar.set(Calendar.DAY_OF_WEEK, weekdays[i])
            dates[i].text = SimpleDateFormat("dd").format(setUpCalendar.time)
            months.add(setUpCalendar.get(Calendar.MONTH))
        }

        if (months[0] != months[6]){
            binding.month.text = monthNames[months[0]]+" - "+monthNames[months[6]]
        }
        else{
            binding.month.text = monthNames[months[0]]
        }

        onWeekdayClick(CurrentDay(
            binding.mondayBtn, binding.monday,
            binding.mondayDate, binding.mondaySlotsIndicator
        ))
    }

    private fun checkSlotsCount(){
        if (slotsList.size >= 2){
            binding.addSlotBtn.visibility = View.GONE
        }
        else {
            binding.addSlotBtn.visibility = View.VISIBLE
        }
        checkEmptyState()
    }

    private fun checkEmptyState() {
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
}