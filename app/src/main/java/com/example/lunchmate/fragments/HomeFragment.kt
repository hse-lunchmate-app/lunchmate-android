package com.example.lunchmatelocal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetFreeSlotBinding
import com.example.lunchmate.databinding.BottomSheetProfileBinding
import com.example.lunchmate.databinding.FragmentHomeBinding
import com.example.lunchmate.model.User
import com.example.lunchmate.utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment: Fragment(R.layout.fragment_home) {
    val weekdaysNames = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    private lateinit var binding: FragmentHomeBinding
    private lateinit var usersList: List<User>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setUpObservers()

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

    private fun setUpObservers() {
        val activity = activity as MainActivity
        activity.viewModel.getUsers("1,2,3").observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.shimmerLayout.visibility = View.GONE
                        resource.data?.let { users ->
                            usersList = users
                            setUpRv(users) }
                    }
                    Status.ERROR -> {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.shimmerLayout.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.recyclerView.visibility = View.INVISIBLE
                        binding.shimmerLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun setUpRv(usersList: List<User>){
        val accountAdapter = ProfilesAdapter(::onProfileClick, usersList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = accountAdapter
    }

    fun filterList(qString: String){
        val filteredList: ArrayList<User> = ArrayList<User>()
        for (account in usersList){
            if (account.name.lowercase().contains(qString.lowercase()))
                filteredList.add(account)
        }

        setUpRv(filteredList as List<User>)
        checkEmptyState(filteredList)
    }

    private fun onProfileClick(user: User) {
        var day_num = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val activity = activity as MainActivity
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val bottomBinding = BottomSheetProfileBinding.bind(layoutInflater.inflate(R.layout.bottom_sheet_profile, null))

        bottomBinding.profileName.text = user.name

        bottomBinding.profileNickname.text = user.login

        bottomBinding.office.text = user.office.name

        bottomBinding.taste.text = user.tastes

        bottomBinding.infoText.text = user.aboutMe

        if (user.messenger != "" && user.messenger != "Без телеграма") {
            bottomBinding.tgButton.visibility = View.VISIBLE
            bottomBinding.tgButton.setOnClickListener {
                val tgIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/" + user.messenger)
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

    private fun checkEmptyState(filteredList: ArrayList<User>) {
        if (filteredList.size == 0) {
            binding.emptyIcon.visibility = View.VISIBLE
            binding.emptyText.visibility = View.VISIBLE
        } else {
            binding.emptyIcon.visibility = View.GONE
            binding.emptyText.visibility = View.GONE
        }
    }
}