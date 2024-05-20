package com.example.lunchmate.domain.model

data class SlotTimetable(
    val id: Int,
    var weekDay: Int,
    var date: String,
    var startTime: String,
    var endTime: String,
    var permanent: Boolean
)