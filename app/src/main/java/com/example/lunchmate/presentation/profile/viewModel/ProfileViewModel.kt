package com.example.lunchmate.presentation.profile.viewModel

import androidx.lifecycle.*
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.Resource
import com.example.lunchmate.domain.model.*
import com.example.lunchmatelocal.Slot
import kotlinx.coroutines.*

class ProfileViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _scheduleData = MutableLiveData<ArrayList<Slot>>()
    val scheduleData: LiveData<ArrayList<Slot>> = _scheduleData
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    fun getFreeSlots(userId: String, date: String, weekday: Int) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val slotsTimetable = mainRepository.getSlotsByDate(userId, date, true)
                val slots: ArrayList<Slot> = ArrayList<Slot>()
                for (slot in slotsTimetable){
                    if (slot.weekDay == weekday)
                        slots.add(Slot(slot, null))
                }
                _scheduleData.postValue(slots)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun inviteForLunch(lunchInvitation: LunchInvitation) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.inviteForLunch(lunchInvitation)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


}