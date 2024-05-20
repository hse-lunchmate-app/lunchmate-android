package com.example.lunchmate.domain.model

data class LunchInvitation(
    val masterId: String,
    val inviteeId: String,
    val timeslotId: Int,
    val lunchDate: String
)
