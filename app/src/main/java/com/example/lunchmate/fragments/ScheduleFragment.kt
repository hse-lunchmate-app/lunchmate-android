package com.example.lunchmatelocal

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.adapters.SlotsAdapter
import com.example.lunchmate.databinding.BottomSheetAddSlotBinding
import com.example.lunchmate.databinding.BottomSheetFreeSlotBinding
import com.example.lunchmate.databinding.BottomSheetReservedSlotBinding
import com.example.lunchmate.databinding.FragmentScheduleBinding
import com.example.lunchmate.utils.MaskWatcher
import com.example.lunchmate.utils.ReservedSlotBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class CurrentDay(
    var btn: RelativeLayout,
    var text: TextView,
    var date: TextView,
    var slotsIndicator: ImageView,
)

class ScheduleFragment: Fragment(R.layout.fragment_schedule) {
    val weekdaysNames = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    val monthNames = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    private lateinit var binding: FragmentScheduleBinding
    private var currentDay: CurrentDay? = null
    private lateinit var slotsAdapter: SlotsAdapter
    lateinit var slotsList: ArrayList<Slot>
    val timeWatcher = MaskWatcher("##:##")
    val calendar: Calendar = Calendar.getInstance()
    var week_num = calendar.get(Calendar.WEEK_OF_YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.bind(view)
        val activity = activity as MainActivity

        slotsList = ArrayList<Slot>()
        slotsList.add(Slot(0, "1 марта","11:00", "12:00", true))
        slotsList.add(Slot(1, "1 марта", "14:00", "15:00", true, activity.currentUser))
        slotsList.add(Slot(2, "1 марта", "14:00", "15:00"))
        setUpRV(slotsList)


        currentDay = setUpCurrentWeek()
        if (currentDay != null){
            currentDay!!.btn.setBackgroundResource(R.drawable.rounded_yellow)
            currentDay!!.text.setTextColor(resources.getColor(R.color.black))
            currentDay!!.date.setTextColor(resources.getColor(R.color.black))
            currentDay!!.slotsIndicator.setColorFilter(resources.getColor(R.color.black))
        }
        binding.mondayBtn.setOnClickListener {
            calendar.set(Calendar.WEEK_OF_YEAR, week_num)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            onWeekdayClick(CurrentDay(
                binding.mondayBtn, binding.monday,
                binding.mondayDate, binding.mondaySlotsIndicator
            ))
        }
        binding.tuesdayBtn.setOnClickListener {
            calendar.set(Calendar.WEEK_OF_YEAR, week_num)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
            onWeekdayClick(CurrentDay(
                binding.tuesdayBtn, binding.tuesday,
                binding.tuesdayDate, binding.tuesdaySlotsIndicator
            ))
        }
        binding.wednesdayBtn.setOnClickListener {
            calendar.set(Calendar.WEEK_OF_YEAR, week_num)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
            onWeekdayClick(CurrentDay(
                binding.wednesdayBtn, binding.wednesday,
                binding.wednesdayDate, binding.wednesdaySlotsIndicator
            ))
        }
        binding.thursdayBtn.setOnClickListener {
            calendar.set(Calendar.WEEK_OF_YEAR, week_num)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
            onWeekdayClick(CurrentDay(
                binding.thursdayBtn, binding.thursday,
                binding.thursdayDate, binding.thursdaySlotsIndicator
            ))
        }
        binding.fridayBtn.setOnClickListener {
            calendar.set(Calendar.WEEK_OF_YEAR, week_num)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            onWeekdayClick(CurrentDay(
                binding.fridayBtn, binding.friday,
                binding.fridayDate, binding.fridaySlotsIndicator
            ))
        }
        binding.addSlotBtn.setOnClickListener {
            addSlot()
        }
        binding.rightButton.setOnClickListener{
            binding.leftButton.isEnabled = true
            setUpWeek(++week_num)
            if (week_num > Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)){
                binding.leftButton.setColorFilter(resources.getColor(R.color.black))
                binding.leftButton.isClickable = true
            }
        }
        binding.leftButton.isEnabled = false
        binding.leftButton.setOnClickListener{
            setUpWeek(--week_num)
            if (week_num <= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)){
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

    private fun checkSlotsCount(){
        if (slotsList.size >= 3){
            binding.addSlotBtn.visibility = View.GONE
        }
        else {
            binding.addSlotBtn.visibility = View.VISIBLE
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment,fragment)
            commit()
        }

    private fun addSlot(){
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val bottomBinding = BottomSheetAddSlotBinding.bind(layoutInflater.inflate(R.layout.bottom_sheet_add_slot, null))

        bottomBinding.date.text = getDateStr(calendar)

        bottomBinding.start.addTextChangedListener(timeWatcher)

        bottomBinding.finish.addTextChangedListener(timeWatcher)

        bottomBinding.addBtn.setOnClickListener {
            if (bottomBinding.start.text.isEmpty() || bottomBinding.finish.text.isEmpty()){
                bottomBinding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.errorMsg.visibility = View.VISIBLE
                bottomBinding.errorMsg.text = "Все поля должны быть заполнены"
            }
            else if (bottomBinding.start.text[2].toString() != ":" || bottomBinding.finish.text[2].toString() != ":"
                ||timeToHour(bottomBinding.start.text.toString()) > 23 || timeToHour(bottomBinding.finish.text.toString()) > 23
                || timeToMinute(bottomBinding.start.text.toString()) > 59 || timeToMinute(bottomBinding.finish.text.toString()) > 59){
                bottomBinding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.errorMsg.visibility = View.VISIBLE
                bottomBinding.errorMsg.text = "Поля должны иметь допустимые значения"
            }
            else if (timeToInt(bottomBinding.start.text.toString()) >= timeToInt(bottomBinding.finish.text.toString())){
                bottomBinding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.errorMsg.visibility = View.VISIBLE
                bottomBinding.errorMsg.text = "Начало должно быть раньше, чем конец"
            }
            else {
                slotsList.add(
                    Slot(
                        0, currentDay!!.date.toString() + " " + binding.month.toString(),
                        bottomBinding.start.text.toString(),
                        bottomBinding.finish.text.toString(),
                        bottomBinding.switchIsRepeating.isChecked
                    )
                )
                dialog.dismiss()
                slotsAdapter.notifyItemInserted(slotsList.size - 1)
                checkSlotsCount()
            }
        }

        dialog.setContentView(bottomBinding.root)
        dialog.show()
    }

    private fun openFreeSlot(position: Int){
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val bottomBinding = BottomSheetFreeSlotBinding.bind(layoutInflater.inflate(R.layout.bottom_sheet_free_slot, null))

        bottomBinding.date.text = getDateStr(calendar)

        bottomBinding.start.setText(slotsList[position].getStart())
        bottomBinding.start.addTextChangedListener(timeWatcher)

        bottomBinding.finish.setText(slotsList[position].getFinish())
        bottomBinding.finish.addTextChangedListener(timeWatcher)

        bottomBinding.switchIsRepeating.isChecked = slotsList[position].getIsRepeating()

        bottomBinding.deleteBtn.setOnClickListener {
            slotsList.removeAt(position)
            dialog.dismiss()
            slotsAdapter.notifyItemRemoved(position)
            checkSlotsCount()
        }

        bottomBinding.saveBtn.setOnClickListener {
            if (bottomBinding.start.text.isEmpty() || bottomBinding.finish.text.isEmpty()){
                bottomBinding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.errorMsg.visibility = View.VISIBLE
                bottomBinding.errorMsg.text = "Все поля должны быть заполнены"
            }
            else if (bottomBinding.start.text[2].toString() != ":" || bottomBinding.finish.text[2].toString() != ":"
                ||timeToHour(bottomBinding.start.text.toString()) > 23 || timeToHour(bottomBinding.finish.text.toString()) > 23
                || timeToMinute(bottomBinding.start.text.toString()) > 59 || timeToMinute(bottomBinding.finish.text.toString()) > 59){
                bottomBinding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.errorMsg.visibility = View.VISIBLE
                bottomBinding.errorMsg.text = "Поля должны иметь допустимые значения"
            }
            else if (timeToInt(bottomBinding.start.text.toString()) >= timeToInt(bottomBinding.finish.text.toString())){
                bottomBinding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                bottomBinding.errorMsg.visibility = View.VISIBLE
                bottomBinding.errorMsg.text = "Начало должно быть раньше, чем конец"
            }
            else {
                slotsList[position].setStart(bottomBinding.start.text.toString())
                slotsList[position].setFinish(bottomBinding.finish.text.toString())
                slotsList[position].setIsRepeating(bottomBinding.switchIsRepeating.isChecked)
                dialog.dismiss()
                slotsAdapter.notifyItemChanged(position)
            }
        }

        dialog.setContentView(bottomBinding.root)
        dialog.show()
    }

    private fun openReservedSlot(position: Int){
        val activity = activity as MainActivity
        val dialog = ReservedSlotBottomSheet(getDateStr(calendar), ::cancelReservation, slotsList, position)
        dialog.show(activity.supportFragmentManager, "")
    }

    private fun cancelReservation(position: Int){
        slotsList[position].setLunchMate(null)
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

        val weekdays = arrayOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)
        val dates = arrayOf(binding.mondayDate, binding.tuesdayDate, binding.wednesdayDate, binding.thursdayDate, binding.fridayDate, binding.saturdayDate, binding.sundayDate)
        val months: ArrayList<Int> = ArrayList<Int>()
        val simpleDateFormat = SimpleDateFormat("dd")
        for (i in 0..weekdays.size - 1){
            setUpCalendar.set(Calendar.DAY_OF_WEEK, weekdays[i])
            dates[i].text = simpleDateFormat.format(setUpCalendar.time)
            months.add(setUpCalendar.get(Calendar.MONTH))
        }

        if (months[0] != months[6]){
            binding.month.setText(monthNames[months[0]]+" - "+monthNames[months[6]])
        }
        else{
            binding.month.setText(monthNames[months[0]])
        }

        when (calendar.get(Calendar.DAY_OF_WEEK)) {
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
        calendar.set(Calendar.WEEK_OF_YEAR, week_num)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val setUpCalendar = Calendar.getInstance()
        setUpCalendar.set(Calendar.WEEK_OF_YEAR, week_num)

        val weekdays = arrayOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)
        val dates = arrayOf(binding.mondayDate, binding.tuesdayDate, binding.wednesdayDate, binding.thursdayDate, binding.fridayDate, binding.saturdayDate, binding.sundayDate)
        val months: ArrayList<Int> = ArrayList<Int>()
        val simpleDateFormat = SimpleDateFormat("dd")
        for (i in 0..weekdays.size - 1){
            setUpCalendar.set(Calendar.DAY_OF_WEEK, weekdays[i])
            dates[i].text = simpleDateFormat.format(setUpCalendar.time)
            months.add(setUpCalendar.get(Calendar.MONTH))
        }

        if (months[0] != months[6]){
            binding.month.setText(monthNames[months[0]]+" - "+monthNames[months[6]])
        }
        else{
            binding.month.setText(monthNames[months[0]])
        }

        onWeekdayClick(CurrentDay(
            binding.mondayBtn, binding.monday,
            binding.mondayDate, binding.mondaySlotsIndicator
        ))
    }

    private fun getDateStr(calendar: Calendar): String{
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK)-1]+SimpleDateFormat(", dd MMM yyyyг.").format(calendar.time)
    }

    private fun timeToInt(time: String): Int{
        return Integer.parseInt(time[0].toString()+time[1]+time[3]+time[4])
    }

    private fun timeToHour(time: String): Int{
        return Integer.parseInt(time[0].toString()+time[1])
    }

    private fun timeToMinute(time: String): Int{
        return Integer.parseInt(time[3].toString()+time[4])
    }
}