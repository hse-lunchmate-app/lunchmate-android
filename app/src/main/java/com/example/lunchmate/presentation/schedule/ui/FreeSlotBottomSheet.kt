package com.example.lunchmate.presentation.schedule.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetFreeSlotBinding
import com.example.lunchmate.databinding.BottomSheetReservedSlotBinding
import com.example.lunchmate.presentation.availableSlots.AvailableSlotsAdapter
import com.example.lunchmate.utils.MaskWatcher
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FreeSlotBottomSheet(
    val date: String,
    val slotsList: ArrayList<Slot>,
    val position: Int,
    val updateSlot: (Int, String, String, Boolean) -> Unit,
    val deleteSlot: (Int) -> Unit,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetFreeSlotBinding
    val weekdaysNames =
        arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    val timeWatcher = MaskWatcher("##:##")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFreeSlotBinding.bind(
            inflater.inflate(
                R.layout.bottom_sheet_free_slot,
                container
            )
        )

        binding.date.text = date

        binding.start.setText(slotsList[position].getStart())
        binding.start.addTextChangedListener(timeWatcher)

        binding.finish.setText(slotsList[position].getFinish())
        binding.finish.addTextChangedListener(timeWatcher)

        binding.switchIsRepeating.isChecked = slotsList[position].getIsRepeating()

        return binding.root
    }

    override fun getTheme() = R.style.SheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        binding.deleteBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            deleteSlot(position)
        }

        binding.saveBtn.setOnClickListener {
            if (binding.start.text.isEmpty() || binding.finish.text.isEmpty()) {
                binding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                binding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                binding.errorMsg.visibility = View.VISIBLE
                binding.errorMsg.text = "Все поля должны быть заполнены"
            } else if (binding.start.text[2].toString() != ":" || binding.finish.text[2].toString() != ":"
                || timeToHour(binding.start.text.toString()) > 23 || timeToHour(binding.finish.text.toString()) > 23
                || timeToMinute(binding.start.text.toString()) > 59 || timeToMinute(binding.finish.text.toString()) > 59
            ) {
                binding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                binding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                binding.errorMsg.visibility = View.VISIBLE
                binding.errorMsg.text = "Поля должны иметь допустимые значения"
            } else if (timeToInt(binding.start.text.toString()) >= timeToInt(binding.finish.text.toString())) {
                binding.start.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                binding.finish.setBackgroundResource(R.drawable.rounded_dark_grey_error)
                binding.errorMsg.visibility = View.VISIBLE
                binding.errorMsg.text = "Начало должно быть раньше, чем конец"
            } else {
                bottomSheetDialog.dismiss()
                updateSlot(position, binding.start.text.toString(), binding.finish.text.toString(), binding.switchIsRepeating.isChecked)
            }
        }

        return bottomSheetDialog
    }

    private fun timeToInt(time: String): Int {
        return Integer.parseInt(time[0].toString() + time[1] + time[3] + time[4])
    }

    private fun timeToHour(time: String): Int {
        return Integer.parseInt(time[0].toString() + time[1])
    }

    private fun timeToMinute(time: String): Int {
        return Integer.parseInt(time[3].toString() + time[4])
    }
}