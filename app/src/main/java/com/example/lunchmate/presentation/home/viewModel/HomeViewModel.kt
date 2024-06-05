package com.example.lunchmate.presentation.home.viewModel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.lunchmate.domain.model.UserPatch
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.Resource
import com.example.lunchmate.domain.model.User
import kotlinx.coroutines.*

class HomeViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _userData = MutableLiveData<List<User>>()
    val userData: LiveData<List<User>> = _userData
    val loadingStateLiveData = MutableLiveData<LoadingState>()
    private var debouncePeriod: Long = 500
    private var searchJob: Job? = null

    fun onSearchQuery(token: String, userId: String, officeId: String, name: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            getUsers(token, userId, officeId, name)
        }
    }

    fun getUsers(token: String, userId: String, officeId: String, name: String) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = mainRepository.getUsers(token, officeId, name) as ArrayList<User>
                users.removeAll { x -> x.id == userId }
                _userData.postValue(users)
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

    fun getUserOffice(token: String, userId: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getUser(token, userId).office))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}