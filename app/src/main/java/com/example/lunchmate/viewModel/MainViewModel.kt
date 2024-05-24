package com.example.lunchmate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.lunchmate.domain.model.UserPatch
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.Resource
import com.example.lunchmate.domain.model.Lunch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getOffices() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getOffices()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getInvitationsCount(userId: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val notifications = mainRepository.getLunches(userId, false) as ArrayList<Lunch>
            notifications.removeAll { x -> x.accepted || x.master.id == userId }
            emit(Resource.success(data = notifications.size))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}