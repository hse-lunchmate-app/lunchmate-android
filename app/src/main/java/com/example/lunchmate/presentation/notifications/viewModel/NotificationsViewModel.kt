package com.example.lunchmate.presentation.notifications.viewModel

import androidx.lifecycle.*
import com.example.lunchmate.MainActivity
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.model.*
import com.example.lunchmatelocal.Slot
import kotlinx.coroutines.*

class NotificationsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _notificationsData = MutableLiveData<ArrayList<Lunch>>()
    val notificationsData: LiveData<ArrayList<Lunch>> = _notificationsData
    val loadingStateLiveData = MutableLiveData<LoadingState>()
    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String> = _toastMsg

    fun getInvitations(userId: String) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val notifications = mainRepository.getLunches(userId, false) as ArrayList<Lunch>
                notifications.removeAll { x -> x.accepted || x.master.id == userId }
                _notificationsData.postValue(notifications)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun getHistory(userId: String) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val notifications = mainRepository.getLunches(userId, false) as ArrayList<Lunch>
                notifications.removeAll { x -> !x.accepted && x.master.id != userId }
                _notificationsData.postValue(notifications)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun acceptInvitation(lunch: Lunch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.acceptInvitation(lunch.id)
                _notificationsData.value?.remove(lunch)
                _notificationsData.postValue(_notificationsData.value)
                _toastMsg.postValue("Приглашение было принято!")
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                _toastMsg.postValue("Не удалось принять приглашение")
            }
        }
    }

    fun declineInvitation(lunch: Lunch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.declineInvitation(lunch.id)
                _notificationsData.value?.remove(lunch)
                _notificationsData.postValue(_notificationsData.value)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun revokeInvitation(lunch: Lunch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.revokeReservation(lunch.id)
                _notificationsData.value?.remove(lunch)
                _notificationsData.postValue(_notificationsData.value)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }
}