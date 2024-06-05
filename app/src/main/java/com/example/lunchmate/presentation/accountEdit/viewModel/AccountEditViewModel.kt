package com.example.lunchmate.presentation.accountEdit.viewModel

import androidx.lifecycle.*
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.Resource
import com.example.lunchmate.domain.model.*
import com.example.lunchmatelocal.Slot
import kotlinx.coroutines.*

class AccountEditViewModel(private val mainRepository: MainRepository) : ViewModel() {

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

    fun patchUser(token: String, id: String, info: UserPatch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.patchUser(token, id, info)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun getOffices(token: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getOffices(token)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}