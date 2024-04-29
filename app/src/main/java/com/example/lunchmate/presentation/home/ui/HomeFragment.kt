package com.example.lunchmate.presentation.home.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetProfileBinding
import com.example.lunchmate.databinding.FragmentHomeBinding
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.presentation.availableSlots.AvailableSlotsAdapter
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.presentation.profile.ProfileBottomSheet
import com.example.lunchmate.presentation.schedule.ui.ReservedSlotBottomSheet
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment: Fragment(R.layout.fragment_home) {
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
        val dialog = ProfileBottomSheet(user)
        dialog.show((activity as MainActivity).supportFragmentManager, "")
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