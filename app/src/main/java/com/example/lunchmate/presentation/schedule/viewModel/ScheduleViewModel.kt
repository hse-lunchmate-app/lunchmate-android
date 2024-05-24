package com.example.lunchmate.presentation.schedule.viewModel

import android.widget.Toast
import androidx.lifecycle.*
import com.example.lunchmate.data.MainRepository
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.Resource
import com.example.lunchmate.domain.model.*
import com.example.lunchmatelocal.Slot
import kotlinx.coroutines.*

class ScheduleViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _scheduleData = MutableLiveData<ArrayList<Slot>>()
    val scheduleData: LiveData<ArrayList<Slot>> = _scheduleData
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    fun getAllSlots(userId: String, date: String, weekday: Int) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val slotsTimetable = mainRepository.getSlotsByDate(userId, date, false)
                val userLunches = mainRepository.getLunches(userId, true)
                val slots: ArrayList<Slot> = ArrayList<Slot>()
                for (slot in slotsTimetable){
                    if (slot.weekDay == weekday) {
                        var lunchMate: User? = null
                        var lunchId: String? = null
                        for (lunch in userLunches){
                            if (lunch.timeslot == slot){
                                if (lunch.master.id == userId){
                                    lunchMate = lunch.invitee
                                    lunchId = lunch.id
                                    break
                                }
                                else if (lunch.invitee.id == userId){
                                    lunchMate = lunch.master
                                    lunchId = lunch.id
                                    break
                                }
                            }
                        }
                        slots.add(Slot(slot, lunchMate, lunchId))
                    }
                }
                _scheduleData.postValue(slots)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun postSlot(slot: SlotPost) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val slotTimetable = mainRepository.postSlot(slot)
                val slots: ArrayList<Slot> = ArrayList<Slot>(_scheduleData.value)
                slots.add(Slot(slotTimetable, null, null))
                slots.sortBy { it.data.startTime }
                _scheduleData.postValue(slots)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun deleteSlot(slot: Slot) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.deleteSlot(slot.data.id.toString())
                _scheduleData.value!!.remove(slot)
                _scheduleData.postValue(_scheduleData.value)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun patchSlot(slot: Slot, slotPatch: SlotPatch) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newSlot = mainRepository.patchSlot(slot.data.id.toString(), slotPatch)
                _scheduleData.value!![_scheduleData.value!!.indexOf(slot)] = Slot(newSlot, null, null)
                _scheduleData.postValue(_scheduleData.value)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun cancelReservation(slot: Slot) {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.revokeReservation(slot.lunchId!!)
                _scheduleData.value!![_scheduleData.value!!.indexOf(slot)].lunchMate = null
                _scheduleData.value!![_scheduleData.value!!.indexOf(slot)].lunchId = null
                _scheduleData.postValue(_scheduleData.value)
                loadingStateLiveData.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }
}