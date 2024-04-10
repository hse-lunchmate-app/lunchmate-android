package com.example.lunchmatelocal

import android.content.Intent
import android.net.Uri
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
import com.example.lunchmate.databinding.BottomSheetFreeSlotBinding
import com.example.lunchmate.databinding.BottomSheetProfileBinding
import com.example.lunchmate.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment: Fragment(R.layout.fragment_home) {
    val weekdaysNames = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    private lateinit var binding: FragmentHomeBinding
    private lateinit var accountsList: ArrayList<Account>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        accountsList = ArrayList<Account>()
        accountsList.add(Account(0, "Иван Иванов 1", 0,"Котлетка с пюрешкой", "Я обычный Иван", "ka_rine_s", "ivan12345", "0000", R.drawable.photo))
        accountsList.add(Account(1, "Иван Иванов 2", 1,"Щи", "Я\nо\nб\nы\nч\nн\nы\nй\nИ\nв\nа\nн", "", "ivan12345", "0000", R.drawable.photo))
        accountsList.add(Account(2, "Иван Иванов 3", 2,"Рассольник", "Я необычный Иван", "ivan123", "ivan12345", "0000", R.drawable.photo))
        accountsList.add(Account(3, "Петр Петров", 0,"Борщ", "Я обычный Петр", "petr123", "petr12345", "0000", R.drawable.photo))

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

        checkEmptyState(filteredList)
    }

    private fun onProfileClick(position: Int) {
        var day_num = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val activity = activity as MainActivity
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val bottomBinding = BottomSheetProfileBinding.bind(layoutInflater.inflate(R.layout.bottom_sheet_profile, null))

        bottomBinding.profileName.text = accountsList[position].getName()

        bottomBinding.profileNickname.text = accountsList[position].getLogin()

        bottomBinding.office.text = activity.offices[accountsList[position].getOffice()]

        bottomBinding.taste.text = accountsList[position].getTaste()

        bottomBinding.infoText.text = accountsList[position].getInfo()

        if (accountsList[position].getTg() != "" && accountsList[position].getTg() != "Без телеграма") {
            bottomBinding.tgButton.visibility = View.VISIBLE
            bottomBinding.tgButton.setOnClickListener {
                val tgIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/" + accountsList[position].getTg())
                )
                startActivity(tgIntent)
            }
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, day_num)
        bottomBinding.date.text = getDateStr(calendar)

        bottomBinding.rightButton.setOnClickListener{
            bottomBinding.leftButton.isEnabled = true
            calendar.set(Calendar.DAY_OF_YEAR, ++day_num)
            bottomBinding.date.text = getDateStr(calendar)
            if (day_num > Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                bottomBinding.leftButton.setColorFilter(resources.getColor(R.color.blue_700))
                bottomBinding.leftButton.isClickable = true
            }
        }
        bottomBinding.leftButton.isEnabled = false
        bottomBinding.leftButton.setOnClickListener{
            calendar.set(Calendar.DAY_OF_YEAR, --day_num)
            bottomBinding.date.text = getDateStr(calendar)
            if (day_num <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                bottomBinding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                bottomBinding.leftButton.isClickable = false
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
        bottomBinding.availableSlots.layoutManager = linearLayoutManager
        bottomBinding.availableSlots.adapter = slotsAdapter

        dialog.setContentView(bottomBinding.root)
        dialog.show()
    }

    private fun getDateStr(calendar: Calendar): String{
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK)-1]+SimpleDateFormat(", dd.MM").format(calendar.time)
    }

    private fun checkEmptyState(filteredList: ArrayList<Account>) {
        if (filteredList.size == 0) {
            binding.emptyIcon.visibility = View.VISIBLE
            binding.emptyText.visibility = View.VISIBLE
        } else {
            binding.emptyIcon.visibility = View.GONE
            binding.emptyText.visibility = View.GONE
        }
    }
}