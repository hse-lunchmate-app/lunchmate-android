package com.example.lunchmate.presentation.home.viewModel

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

    fun onSearchQuery(officeId: String, name: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            getUsers(officeId, name)
        }
    }

    fun getUsers(officeId: String, name: String) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val movies = mainRepository.getUsers(officeId, name)
                _userData.postValue(movies)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }
}