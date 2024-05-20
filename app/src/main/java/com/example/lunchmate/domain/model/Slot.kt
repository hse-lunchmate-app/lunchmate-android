package com.example.lunchmatelocal

import com.example.lunchmate.domain.model.SlotTimetable
import com.example.lunchmate.domain.model.User

data class Slot(
    var data: SlotTimetable,
    var lunchMate: User?
)