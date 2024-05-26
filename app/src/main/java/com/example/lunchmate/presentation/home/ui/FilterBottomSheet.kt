package com.example.lunchmate.presentation.home.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetFilterBinding
import com.example.lunchmate.domain.model.Office
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet(
    val userOffice: Office,
    val currentOffice: Office,
    private val offices: List<Office>,
    private val officeNames: List<String>,
    val filterSearch: (Office) -> Unit,
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

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            officeNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOffice.adapter = adapter
        binding.spinnerOffice.setSelection(offices.indexOf(currentOffice))

        binding.clearBtn.setOnClickListener {
            binding.spinnerOffice.setSelection(offices.indexOf(userOffice))
        }

        binding.searchBtn.setOnClickListener {
            filterSearch(offices[binding.spinnerOffice.selectedItemPosition])
            dismiss()
        }

        return binding.root
    }

    override fun getTheme() = R.style.SheetDialog
}