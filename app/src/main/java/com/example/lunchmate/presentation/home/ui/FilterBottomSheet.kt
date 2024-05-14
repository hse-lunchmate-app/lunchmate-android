package com.example.lunchmate.presentation.home.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetFilterBinding
import com.example.lunchmate.databinding.BottomSheetFreeSlotBinding
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.collections.ArrayList

class FilterBottomSheet(
    val position: Int,
    val filterSearch: (Int) -> Unit,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFilterBinding.bind(
            inflater.inflate(
                R.layout.bottom_sheet_filter,
                container
            )
        )


        return binding.root
    }

    override fun getTheme() = R.style.SheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            binding.searchBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                filterSearch(position)
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