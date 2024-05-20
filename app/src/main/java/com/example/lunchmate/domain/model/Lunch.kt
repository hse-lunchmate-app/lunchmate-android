package com.example.lunchmate.domain.model

data class Lunch(
    val id: String,
    val master: User,
    val invitee: User,
    val timeslot: SlotTimetable,
    val accepted: Boolean,
    val lunchDate: String,
    val createDate: String
)
