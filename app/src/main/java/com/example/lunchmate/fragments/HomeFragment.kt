package com.example.lunchmatelocal

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
import com.example.lunchmate.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var accountsList: ArrayList<Account>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        accountsList = ArrayList<Account>()
        accountsList.add(Account("Иван Иванов 1", 0,"Котлетка с пюрешкой", "Я обычный Иван", "ivan123", "ivan12345", "0000", R.drawable.photo))
        accountsList.add(Account("Иван Иванов 2", 1,"Щи", "Я\nо\nб\nы\nч\nн\nы\nй\nИ\nв\nа\nн", "ivan123", "ivan12345", "0000", R.drawable.photo))
        accountsList.add(Account("Иван Иванов 3", 2,"Рассольник", "Я необычный Иван", "ivan123", "ivan12345", "0000", R.drawable.photo))
        accountsList.add(Account("Петр Петров", 0,"Борщ", "Я обычный Петр", "petr123", "petr12345", "0000", R.drawable.photo))

        val accountAdapter = ProfilesAdapter(::onProfileClick, accountsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = accountAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                filterList(qString)
                return true
            }
            override fun onQueryTextSubmit(qString: String): Boolean {
                return false
            }
        })
    }

    fun filterList(qString: String){
        val filteredList: ArrayList<Account> = ArrayList<Account>()
        for (account in accountsList){
            if (account.getName().lowercase().contains(qString.lowercase()))
                filteredList.add(account)
        }
        val courseAdapter = ProfilesAdapter(::onProfileClick, filteredList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = courseAdapter
    }

    fun onProfileClick(position: Int) {
        val activity = activity as MainActivity
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_profile, null)

        val profileName = view.findViewById<TextView>(R.id.profileName)
        profileName.setText(accountsList[position].getName())

        val profileNickname = view.findViewById<TextView>(R.id.profileNickname)
        profileNickname.setText(accountsList[position].getLogin())

        val profileOffice = view.findViewById<TextView>(R.id.office)
        profileOffice.setText(activity.offices[accountsList[position].getOffice()])

        val profileTaste = view.findViewById<TextView>(R.id.taste)
        profileTaste.setText(accountsList[position].getTaste())

        val profileInfo = view.findViewById<TextView>(R.id.infoText)
        profileInfo.setText(accountsList[position].getInfo())

        val availableSlots = view.findViewById<RecyclerView>(R.id.availableSlots)
        val slotsList = ArrayList<Slot>()
        slotsList.add(Slot("11:00", "12:00"))
        slotsList.add(Slot("14:00", "15:00"))
        slotsList.add(Slot("14:00", "15:00"))
        slotsList.add(Slot("14:00", "15:00"))
        slotsList.add(Slot("14:00", "15:00"))
        val slotsAdapter = AvailableSlotsAdapter(slotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        availableSlots.layoutManager = linearLayoutManager
        availableSlots.adapter = slotsAdapter

        val btn = view.findViewById<ImageButton>(R.id.leftButton)
        btn.isClickable = true

        dialog.setContentView(view)
        dialog.show()
    }

}