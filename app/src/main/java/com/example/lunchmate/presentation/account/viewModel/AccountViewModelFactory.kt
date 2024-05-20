package com.example.lunchmate.presentation.account.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.data.MainRepository

class AccountViewModelFactory(private val apiHelper: ApiHelper) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}