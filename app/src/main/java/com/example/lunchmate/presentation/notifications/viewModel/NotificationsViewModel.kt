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
    val toastMsg = MutableLiveData<String>()

    fun getInvitations(token: String, userId: String) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val notifications = mainRepository.getLunches(token, userId, false) as ArrayList<Lunch>
                notifications.removeAll { x -> x.accepted || x.master.id == userId }
                _notificationsData.postValue(notifications)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
                toastMsg.postValue("Error Occurred!")
            }
        }
    }

    fun getHistory(token: String, userId: String) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val notifications = mainRepository.getLunches(token, userId, false) as ArrayList<Lunch>
                notifications.removeAll { x -> !x.accepted && x.master.id != userId }
                _notificationsData.postValue(notifications)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
                toastMsg.postValue("Error Occurred!")
            }
        }
    }

    fun acceptInvitation(token: String, lunch: Lunch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.acceptInvitation(token, lunch.id)
                _notificationsData.value?.remove(lunch)
                _notificationsData.postValue(_notificationsData.value)
                toastMsg.postValue("Приглашение было принято!")
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
                toastMsg.postValue("Не удалось принять приглашение")
            }
        }
    }

    fun declineInvitation(token: String, lunch: Lunch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.declineInvitation(token, lunch.id)
                _notificationsData.value?.remove(lunch)
                _notificationsData.postValue(_notificationsData.value)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
                toastMsg.postValue("Error Occurred!")
            }
        }
    }

    fun revokeInvitation(token: String, lunch: Lunch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.revokeReservation(token, lunch.id)
                _notificationsData.value?.remove(lunch)
                _notificationsData.postValue(_notificationsData.value)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
                toastMsg.postValue("Error Occurred!")
            }
        }
    }
}