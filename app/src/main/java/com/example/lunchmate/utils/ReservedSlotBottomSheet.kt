package com.example.lunchmate.utils

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetReservedSlotBinding
import com.example.lunchmatelocal.AvailableSlotsAdapter
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class ReservedSlotBottomSheet(val slotsList: ArrayList<Slot>, val position: Int): BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetReservedSlotBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetReservedSlotBinding.bind(inflater.inflate(R.layout.bottom_sheet_reserved_slot, container))
        binding.start.setText(slotsList[position].getStart())

        binding.finish.setText(slotsList[position].getFinish())

        val lunchMateAccount = slotsList[position].getLunchMate()

        binding.lunchMate.setText(lunchMateAccount?.getName() ?: "Без имени")

        binding.profileNickname.setText(lunchMateAccount?.getLogin())

        val activity = activity as MainActivity
        binding.office.setText(activity.offices[lunchMateAccount?.getOffice()!!])

        binding.taste.setText(lunchMateAccount?.getTaste())

        binding.infoText.setText(lunchMateAccount?.getInfo())

        val availableSlotsList = ArrayList<Slot>()
        availableSlotsList.add(Slot("1 марта", "11:00", "12:00"))
        availableSlotsList.add(Slot("1 марта", "14:00", "15:00"))
        availableSlotsList.add(Slot("1 марта", "14:00", "15:00"))
        availableSlotsList.add(Slot("1 марта", "14:00", "15:00"))
        availableSlotsList.add(Slot("1 марта", "14:00", "15:00"))
        val slotsAdapter = AvailableSlotsAdapter(availableSlotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.availableSlots.layoutManager = linearLayoutManager
        binding.availableSlots.adapter = slotsAdapter

        binding.lunchMate.setOnClickListener {
            if (binding.scrollView.isVisible){
                binding.lunchMate.setIcon(resources.getDrawable(R.drawable.arrow_right))
                binding.scrollView.visibility = View.GONE
            }
            else{
                binding.lunchMate.setIcon(resources.getDrawable(R.drawable.arrow_down))
                binding.scrollView.visibility = View.VISIBLE
            }
        }

        if (slotsList[position].getIsRepeating()){
            binding.repeatingIndicator.visibility = View.VISIBLE
        }
        return binding.root
    }

    override fun getTheme() = R.style.SheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            val coordinator = (it as BottomSheetDialog)
                .findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            val containerLayout =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.container)
            val buttons = bottomSheetDialog.layoutInflater.inflate(R.layout.item_sticky_button, null)

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