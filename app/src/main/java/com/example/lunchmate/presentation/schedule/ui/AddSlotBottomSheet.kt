package com.example.lunchmate.presentation.schedule.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetAddSlotBinding
import com.example.lunchmate.domain.model.SlotPost
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddSlotBottomSheet(
    private val userId: String,
    val date: String,
    private val currentDate: String,
    val addSlot: (SlotPost) -> Unit,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetAddSlotBinding
    private val timeWatcher = MaskWatcher("##:##")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAddSlotBinding.bind(
            inflater.inflate(
                R.layout.bottom_sheet_add_slot,
                container
            )
        )

        binding.date.text = date

        binding.start.addTextChangedListener(timeWatcher)

        binding.finish.addTextChangedListener(timeWatcher)

        binding.addBtn.setOnClickListener {
            if (binding.start.text.length < 5 || binding.finish.text.length < 5) {
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
                addSlot(
                    SlotPost(
                        userId,
                        currentDate,
                        binding.start.text.toString(),
                        binding.finish.text.toString(),
                        binding.switchIsRepeating.isChecked
                    )
                )
                dismiss()
            }
        }

        return binding.root
    }

    override fun getTheme() = R.style.SheetDialog

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