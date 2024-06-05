package com.example.lunchmate.presentation.account.viewModel

import androidx.lifecycle.*
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.model.*
import com.example.lunchmatelocal.Slot
import kotlinx.coroutines.*
import retrofit2.http.Body

class AccountViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _accountData = MutableLiveData<User>()
    val accountData: LiveData<User> = _accountData
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    fun getUser(token: String, id: String) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = mainRepository.getUser(token, id)
                _accountData.postValue(user)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }
}