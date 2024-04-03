package com.example.lunchmatelocal

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.FragmentAccountEditBinding
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
        bottomNav.setVisibility(View.GONE)

        val activity = activity as MainActivity
        binding.photo.setImageResource(activity.currentUser.getPhoto())
        binding.edittextName.setText(activity.currentUser.getName())
        binding.edittextLogin.setText(activity.currentUser.getLogin())
        binding.edittextTg.setText(activity.currentUser.getTg())
        binding.spinnerOffice.setSelection(activity.currentUser.getOffice())
        binding.edittextTaste.setText(activity.currentUser.getTaste())
        binding.edittextInfo.setText(activity.currentUser.getInfo())
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, activity.offices)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOffice.setAdapter(adapter)

        binding.backButton.setOnClickListener {
            setCurrentFragment(accountFragment)
        }

        binding.saveButton.setOnClickListener {
            activity.currentUser.setName(binding.edittextName.text.toString())
            activity.currentUser.setLogin(binding.edittextLogin.text.toString())
            activity.currentUser.setTg(binding.edittextTg.text.toString())
            activity.currentUser.setOffice(binding.spinnerOffice.selectedItemPosition)
            activity.currentUser.setTaste(binding.edittextTaste.text.toString())
            activity.currentUser.setInfo(binding.edittextInfo.text.toString())
            setCurrentFragment(accountFragment)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, fragment)
            commit()
        }

}