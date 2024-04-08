package com.example.lunchmate.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.adapters.NotificationsAdapter
import com.example.lunchmate.adapters.SlotsAdapter
import com.example.lunchmate.databinding.FragmentNotificationsBinding
import com.example.lunchmate.model.Notification
import com.example.lunchmatelocal.Account
import com.example.lunchmatelocal.AvailableSlotsAdapter
import com.example.lunchmatelocal.ProfilesAdapter
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog

class NotificationsFragment: Fragment(R.layout.fragment_notifications) {
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationsList: ArrayList<Notification>
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationsBinding.bind(view)
        val activity = activity as MainActivity

        notificationsList = ArrayList<Notification>()
        notificationsList.add(Notification("Новое приглашение", Slot("1 марта", "11:00", "12:00"), activity.currentUser))
        notificationsList.add(Notification("Отказ", Slot("1 марта", "11:00", "12:00"), activity.currentUser))
        notificationsList.add(Notification("Согласие", Slot("1 марта", "11:00", "12:00"), activity.currentUser))
        notificationsList.add(Notification("Напоминание", Slot("1 марта", "11:00", "12:00"), activity.currentUser))

        setUpRV(notificationsList)
    }

    fun onProfileClick(position: Int) {
        val activity = activity as MainActivity
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_profile, null)

        val profileName = view.findViewById<TextView>(R.id.profileName)
        profileName.setText(notificationsList[position].getLunchMate().getName())

        val profileNickname = view.findViewById<TextView>(R.id.profileNickname)
        profileNickname.setText(notificationsList[position].getLunchMate().getLogin())

        val profileOffice = view.findViewById<TextView>(R.id.office)
        profileOffice.setText(activity.offices[notificationsList[position].getLunchMate().getOffice()])

        val profileTaste = view.findViewById<TextView>(R.id.taste)
        profileTaste.setText(notificationsList[position].getLunchMate().getTaste())

        val profileInfo = view.findViewById<TextView>(R.id.infoText)
        profileInfo.setText(notificationsList[position].getLunchMate().getInfo())

        val availableSlots = view.findViewById<RecyclerView>(R.id.availableSlots)
        val slotsList = ArrayList<Slot>()
        slotsList.add(Slot("1 марта", "11:00", "12:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        val slotsAdapter = AvailableSlotsAdapter(slotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        availableSlots.layoutManager = linearLayoutManager
        availableSlots.adapter = slotsAdapter

        val btn = view.findViewById<ImageButton>(R.id.leftButton)
        btn.isClickable = true

        dialog.setContentView(view)
        dialog.show()
    }

    fun denyRequest(position: Int){
        notificationsList.removeAt(position)
        notificationsAdapter.notifyItemRemoved(position)
    }

    private fun setUpRV(notificationsList: ArrayList<Notification>){
        notificationsAdapter = NotificationsAdapter(::onProfileClick, ::denyRequest, notificationsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = notificationsAdapter
    }
}