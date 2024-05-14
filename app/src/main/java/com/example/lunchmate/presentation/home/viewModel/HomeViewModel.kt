package com.example.lunchmate.presentation.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.lunchmate.domain.model.UserPatch
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.Resource
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val mainRepository: MainRepository) : ViewModel() {



    fun getOffices() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getOffices()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getUsers(officeId: String, name: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getUsers(officeId, name)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getUser(id: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getUser(id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun patchUser(id: String, info: UserPatch) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.patchUser(id, info)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}