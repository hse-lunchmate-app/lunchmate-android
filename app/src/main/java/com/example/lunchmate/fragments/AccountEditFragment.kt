package com.example.lunchmatelocal

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentAccountEditBinding
import com.example.lunchmate.model.Office
import com.example.lunchmate.model.User
import com.example.lunchmate.model.UserPatch
import com.example.lunchmate.utils.Status
import com.google.android.material.bottomnavigation.BottomNavigationView


class AccountEditFragment: Fragment(R.layout.fragment_account_edit) {
    private lateinit var binding: FragmentAccountEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountEditBinding.bind(view)
        val accountFragment = AccountFragment()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.GONE

        val activity = activity as MainActivity
        setUpObserver()

        binding.backButton.setOnClickListener {
            setCurrentFragment(accountFragment)
        }

        binding.saveButton.setOnClickListener {
            activity.viewModel.patchUser("1", UserPatch(
                binding.edittextName.text.toString(),
                binding.edittextTg.text.toString(),
                binding.edittextTaste.text.toString(),
                binding.edittextInfo.text.toString(),
                activity.offices[binding.spinnerOffice.selectedItemPosition].id
            )).observe(viewLifecycleOwner) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { user ->
                                activity.currentUser = user
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            }
            setCurrentFragment(accountFragment)
        }
    }

    private fun setUpObserver() {
        val activity = activity as MainActivity
        activity.viewModel.getUser("1").observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { user ->
                            activity.currentUser = user
                            setUpCurrentUser(activity.currentUser) }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun setUpCurrentUser(currentUser: User) {
        //binding.photo.setImageResource(currentUser.getPhoto())
        binding.edittextName.setText(currentUser.name)
        binding.edittextLogin.setText("@"+currentUser.login)
        binding.edittextTg.setText(currentUser.messenger)
        binding.edittextTaste.setText(currentUser.tastes)
        binding.edittextInfo.setText(currentUser.aboutMe)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, (activity as MainActivity).officeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOffice.adapter = adapter
        binding.spinnerOffice.setSelection((activity as MainActivity).offices.indexOf(currentUser.office))
    }

    private fun setCurrentFragment(fragment: Fragment) =
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, fragment)
            commit()
        }

}