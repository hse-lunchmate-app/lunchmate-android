package com.example.lunchmate.domain.model

import com.example.lunchmatelocal.Slot
import java.util.*
import kotlin.collections.ArrayList

data class Day(
    val date: String,
    val weekDay: Int,
    var slotsList: ArrayList<Slot> = ArrayList<Slot>(),
    private var lunchCount: Int = 0
) {
    fun addSlot(slot: Slot) {
        slotsList.add(slot)
        if (slot.lunchMate != null) {
            lunchCount++
        }
        slotsList.sortBy { it.data.startTime }
    }

    fun deleteSlot(slot: Slot) {
        slotsList.remove(slot)
        if (slot.lunchMate != null) {
            lunchCount--
        }
    }
}