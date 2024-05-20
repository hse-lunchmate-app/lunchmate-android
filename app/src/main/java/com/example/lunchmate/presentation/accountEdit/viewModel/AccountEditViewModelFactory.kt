package com.example.lunchmate.presentation.accountEdit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.data.MainRepository

class AccountEditViewModelFactory(private val apiHelper: ApiHelper) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountEditViewModel::class.java)) {
            return AccountEditViewModel(MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}