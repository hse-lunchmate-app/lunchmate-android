package com.example.lunchmate.presentation.profile

import android.app.Dialog
import android.content.Intent
import android.net.Uri
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
import com.example.lunchmate.databinding.BottomSheetProfileBinding
import com.example.lunchmate.databinding.BottomSheetReservedSlotBinding
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.availableSlots.AvailableSlotsAdapter
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileBottomSheet(
    val user: User
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetProfileBinding
    val weekdaysNames =
        arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetProfileBinding.bind(
            inflater.inflate(
                R.layout.bottom_sheet_profile,
                container
            )
        )

        binding.profileName.text = user.name

        binding.profileNickname.text = user.login

        binding.office.text = user.office.name

        binding.taste.text = user.tastes

        binding.infoText.text = user.aboutMe

        binding.tgButton.setOnClickListener {
            val tgIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://t.me/" + user.messenger)
            )
            startActivity(tgIntent)

        }

        val calendar = Calendar.getInstance()
        var day_num = calendar.get(Calendar.DAY_OF_YEAR)
        binding.date.text = getDateStr(calendar)

        binding.rightButton.setOnClickListener{
            binding.leftButton.isEnabled = true
            calendar.set(Calendar.DAY_OF_YEAR, ++day_num)
            binding.date.text = getDateStr(calendar)
            if (day_num > Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                binding.leftButton.setColorFilter(resources.getColor(R.color.blue_700))
                binding.leftButton.isClickable = true
            }
        }
        binding.leftButton.isEnabled = false
        binding.leftButton.setOnClickListener{
            calendar.set(Calendar.DAY_OF_YEAR, --day_num)
            binding.date.text = getDateStr(calendar)
            if (day_num <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                binding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                binding.leftButton.isClickable = false
            }
        }

        val slotsList = ArrayList<Slot>()
        slotsList.add(Slot(0, "1 марта","11:00", "12:00"))
        slotsList.add(Slot(1, "1 марта", "14:00", "15:00"))
        slotsList.add(Slot(2, "1 марта", "14:00", "15:00"))
        slotsList.add(Slot(3, "1 марта", "14:00", "15:00"))
        slotsList.add(Slot(4, "1 марта", "14:00", "15:00"))
        val slotsAdapter = AvailableSlotsAdapter(slotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.availableSlots.layoutManager = linearLayoutManager
        binding.availableSlots.adapter = slotsAdapter

        return binding.root
    }

    override fun getTheme() = R.style.SheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        return bottomSheetDialog
    }

    private fun getDateStr(calendar: Calendar): String {
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK) - 1] + SimpleDateFormat(", dd.MM").format(
            calendar.time
        )
    }
}