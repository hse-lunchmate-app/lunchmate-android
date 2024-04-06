package com.example.lunchmatelocal

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.adapters.SlotsAdapter
import com.example.lunchmate.databinding.FragmentScheduleBinding
import com.example.lunchmate.utils.MaskWatcher
import com.example.lunchmate.utils.ReservedSlotBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

data class CurrentDay(
    var btn: RelativeLayout,
    var text: TextView,
    var date: TextView,
    var slotsIndicator: ImageView,
)

class ScheduleFragment: Fragment(R.layout.fragment_schedule) {
    private lateinit var binding: FragmentScheduleBinding
    private lateinit var currentDay: CurrentDay
    lateinit var slotsList: ArrayList<Slot>
    val timeWatcher = MaskWatcher("##:##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.bind(view)
        val activity = activity as MainActivity

        slotsList = ArrayList<Slot>()
        slotsList.add(Slot("11:00", "12:00", true))
        slotsList.add(Slot("14:00", "15:00", true, activity.currentUser))
        slotsList.add(Slot("14:00", "15:00"))
        setUpSlotsRV(slotsList)

        currentDay = CurrentDay(
            binding.wednesdayBtn, binding.wednesday,
            binding.wednesdayDate, binding.wednesdaySlotsIndicator
        )
        binding.mondayBtn.setOnClickListener {
            onWeekdayClick(CurrentDay(
                binding.mondayBtn, binding.monday,
                binding.mondayDate, binding.mondaySlotsIndicator
            ))
        }
        binding.tuesdayBtn.setOnClickListener {
            onWeekdayClick(CurrentDay(
                binding.tuesdayBtn, binding.tuesday,
                binding.tuesdayDate, binding.tuesdaySlotsIndicator
            ))
        }
        binding.wednesdayBtn.setOnClickListener {
            onWeekdayClick(CurrentDay(
                binding.wednesdayBtn, binding.wednesday,
                binding.wednesdayDate, binding.wednesdaySlotsIndicator
            ))
        }
        binding.thursdayBtn.setOnClickListener {
            onWeekdayClick(CurrentDay(
                binding.thursdayBtn, binding.thursday,
                binding.thursdayDate, binding.thursdaySlotsIndicator
            ))
        }
        binding.fridayBtn.setOnClickListener {
            onWeekdayClick(CurrentDay(
                binding.fridayBtn, binding.friday,
                binding.fridayDate, binding.fridaySlotsIndicator
            ))
        }
        binding.addSlotBtn.setOnClickListener {
            addSlot()
            if (slotsList.size >= 4){
                binding.addSlotBtn.visibility = View.GONE
            }
        }
    }

    private fun setUpSlotsRV(slotsList: ArrayList<Slot>){
        val slotsAdapter = SlotsAdapter(::openFreeSlot, ::openReservedSlot, slotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.slotsRV.layoutManager = linearLayoutManager
        binding.slotsRV.adapter = slotsAdapter
    }

    private fun setCurrentFragment(fragment: Fragment)=
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment,fragment)
            commit()
        }

    private fun addSlot(){
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_slot, null)

        val start = view.findViewById<EditText>(R.id.start)
        start.addTextChangedListener(timeWatcher)

        val finish = view.findViewById<EditText>(R.id.finish)
        finish.addTextChangedListener(timeWatcher)

        val isRepeating = view.findViewById<SwitchMaterial>(R.id.switchIsRepeating)

        val addBtn = view.findViewById<AppCompatButton>(R.id.addBtn)
        addBtn.setOnClickListener {
            slotsList.add(Slot(start.text.toString(), finish.text.toString(), isRepeating.isChecked))
            dialog.dismiss()
            setUpSlotsRV(slotsList)
        }

        dialog.setContentView(view)
        dialog.show()
    }

    fun openFreeSlot(position: Int){
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_free_slot, null)

        val start = view.findViewById<EditText>(R.id.start)
        start.setText(slotsList[position].getStart())
        start.addTextChangedListener(timeWatcher)

        val finish = view.findViewById<EditText>(R.id.finish)
        finish.setText(slotsList[position].getFinish())
        finish.addTextChangedListener(timeWatcher)

        val switch = view.findViewById<SwitchMaterial>(R.id.switchIsRepeating)
        switch.setChecked(slotsList[position].getIsRepeating())

        val deleteBtn = view.findViewById<AppCompatButton>(R.id.deleteBtn)
        deleteBtn.setOnClickListener {
            slotsList.removeAt(position)
            dialog.dismiss()
            setUpSlotsRV(slotsList)
        }

        val saveBtn = view.findViewById<AppCompatButton>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            slotsList[position].setStart(start.text.toString())
            slotsList[position].setFinish(finish.text.toString())
            slotsList[position].setIsRepeating(switch.isChecked)
            dialog.dismiss()
            setUpSlotsRV(slotsList)
        }

        dialog.setContentView(view)
        dialog.show()
    }

    fun openReservedSlot(position: Int){
        val activity = activity as MainActivity
        val dialog = ReservedSlotBottomSheet(slotsList, position)


        dialog.show(activity.supportFragmentManager, "")
    }

    private fun onWeekdayClick(newDay: CurrentDay) {
        if (newDay.btn != currentDay.btn) {
            currentDay.btn.setBackgroundResource(R.drawable.rounded_blue_stroke)
            currentDay.text.setTextColor(getResources().getColor(R.color.blue_700))
            currentDay.date.setTextColor(getResources().getColor(R.color.blue_700))
            currentDay.slotsIndicator.setColorFilter(getResources().getColor(R.color.blue_700))
            currentDay.btn = newDay.btn
            currentDay.text = newDay.text
            currentDay.date = newDay.date
            currentDay.slotsIndicator = newDay.slotsIndicator
            newDay.btn.setBackgroundResource(R.drawable.rounded_yellow)
            newDay.text.setTextColor(getResources().getColor(R.color.black))
            newDay.date.setTextColor(getResources().getColor(R.color.black))
            newDay.slotsIndicator.setColorFilter(getResources().getColor(R.color.black))
        }
    }
}