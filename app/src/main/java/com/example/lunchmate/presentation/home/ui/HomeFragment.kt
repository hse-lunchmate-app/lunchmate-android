package com.example.lunchmate.presentation.home.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentHomeBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.domain.model.City
import com.example.lunchmate.domain.model.Office
import com.example.lunchmate.presentation.home.viewModel.HomeViewModel
import com.example.lunchmate.presentation.home.viewModel.HomeViewModelFactory
import com.example.lunchmate.presentation.profile.ui.ProfileBottomSheet


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userOffice: Office
    private lateinit var currentOffice: Office
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = requireActivity().getSharedPreferences("CurrentUserInfo",AppCompatActivity.MODE_PRIVATE).getString("userId", "")!!
        homeViewModel = ViewModelProvider(
            requireActivity(), HomeViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        )[HomeViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        homeViewModel.getUserOffice(userId).observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { office ->
                            userOffice = office
                            currentOffice = office
                            initialiseObservers()
                            initialiseUIElements()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {}
                }
            }
        }
    }

    private fun initialiseObservers() {
        homeViewModel.getUsers(
            userId,
            currentOffice.id.toString(),
            binding.searchView.query.toString()
        )

        homeViewModel.userData.observe(viewLifecycleOwner) {
            setUpRV(it)
        }

        homeViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }
    }

    private fun initialiseUIElements() {
        if (currentOffice.id == userOffice.id) {
            binding.filterBtn.setColorFilter(resources.getColor(R.color.black))
        } else {
            binding.filterBtn.setColorFilter(resources.getColor(R.color.yellow_700))
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                homeViewModel.onSearchQuery(
                    userId,
                    currentOffice.id.toString(),
                    qString
                )
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {
                return false
            }
        })

        binding.filterBtn.setOnClickListener {
            openFilter()
        }
    }

    private fun setUpRV(usersList: List<User>) {
        val accountAdapter = ProfilesAdapter(::onProfileClick, usersList)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = accountAdapter
        checkEmptyState(usersList)
    }

    private fun onProfileClick(user: User) {
        val dialog = ProfileBottomSheet(userId, user)
        dialog.show((activity as MainActivity).supportFragmentManager, "")
    }

    private fun openFilter() {
        homeViewModel.getOffices().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { officeList ->
                            val officeNames = ArrayList<String>()
                            for (office in officeList)
                                officeNames.add(office.name)
                            val dialog = FilterBottomSheet(
                                currentOffice,
                                officeList,
                                officeNames,
                                ::filterSearch
                            )
                            dialog.show((activity as MainActivity).supportFragmentManager, "")
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {}
                }
            }
        }
    }

    private fun filterSearch(current_office: Office) {
        currentOffice = current_office
        if (currentOffice.id == userOffice.id) {
            binding.filterBtn.setColorFilter(resources.getColor(R.color.black))
        } else {
            binding.filterBtn.setColorFilter(resources.getColor(R.color.yellow_700))
        }
        homeViewModel.getUsers(userId, currentOffice.id.toString(), binding.searchView.query.toString())
    }

    private fun checkEmptyState(list: List<User>): Boolean {
        if (list.isEmpty()) {
            binding.emptyIcon.visibility = View.VISIBLE
            binding.emptyText.visibility = View.VISIBLE
            return true
        }
        binding.emptyIcon.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
        return false
    }

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            LoadingState.LOADING -> {
                binding.recyclerView.visibility = View.INVISIBLE
                binding.shimmerLayout.visibility = View.VISIBLE
            }
            LoadingState.ERROR -> {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_LONG).show()
            }
        }
    }
}