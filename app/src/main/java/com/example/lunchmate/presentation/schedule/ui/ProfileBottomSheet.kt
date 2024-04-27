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
import com.example.lunchmate.databinding.BottomSheetReservedSlotBinding
import com.example.lunchmate.presentation.availableSlots.AvailableSlotsAdapter
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileBottomSheet(val date: String, val cancelReservation: (Int) -> Unit, val slotsList: ArrayList<Slot>, val position: Int): BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetReservedSlotBinding
    val weekdaysNames = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetReservedSlotBinding.bind(inflater.inflate(R.layout.bottom_sheet_reserved_slot, container))

        binding.date.text = date

        binding.start.text = slotsList[position].getStart()

        binding.finish.text = slotsList[position].getFinish()

        val lunchMateAccount = slotsList[position].getLunchMate()

        binding.lunchMate.text = lunchMateAccount?.getName()

        binding.profileNickname.text = lunchMateAccount?.getLogin()

        val activity = activity as MainActivity
        //binding.office.text = activity.offices[lunchMateAccount?.getOffice()!!]

        //binding.taste.text = lunchMateAccount.getTaste()

        //binding.infoText.text = lunchMateAccount.getInfo()

        /*if (lunchMateAccount.getTg() != "" && lunchMateAccount.getTg() != "Без телеграма") {
            binding.tgButton.visibility = View.VISIBLE
            binding.tgButton.setOnClickListener {
                val tgIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/" + lunchMateAccount.getTg())
                )
                startActivity(tgIntent)
            }
        }*/

        var day_num = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, day_num)
        binding.dateSchedule.text = getDateStr(calendar)

        binding.rightButton.setOnClickListener{
            binding.leftButton.isEnabled = true
            calendar.set(Calendar.DAY_OF_YEAR, ++day_num)
            binding.dateSchedule.text = getDateStr(calendar)
            if (day_num > Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                binding.leftButton.setColorFilter(resources.getColor(R.color.blue_700))
                binding.leftButton.isClickable = true
            }
        }
        binding.leftButton.isEnabled = false
        binding.leftButton.setOnClickListener{
            calendar.set(Calendar.DAY_OF_YEAR, --day_num)
            binding.dateSchedule.text = getDateStr(calendar)
            if (day_num <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                binding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                binding.leftButton.isClickable = false
            }
        }

        val availableSlotsList = ArrayList<Slot>()
        availableSlotsList.add(Slot(0, "1 марта", "11:00", "12:00"))
        availableSlotsList.add(Slot(0, "1 марта", "14:00", "15:00"))
        availableSlotsList.add(Slot(0, "1 марта", "14:00", "15:00"))
        availableSlotsList.add(Slot(0, "1 марта", "14:00", "15:00"))
        availableSlotsList.add(Slot(0, "1 марта", "14:00", "15:00"))
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

            val cancelBtn = buttons.findViewById<AppCompatButton>(R.id.cancelBtn)
            cancelBtn.setOnClickListener {
                cancelReservation(position)
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

    private fun getDateStr(calendar: Calendar): String{
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK)-1]+ SimpleDateFormat(", dd.MM").format(calendar.time)
    }
}