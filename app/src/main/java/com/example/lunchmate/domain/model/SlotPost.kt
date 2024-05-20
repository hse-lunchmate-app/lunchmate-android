package com.example.lunchmate.domain.model

data class SlotPost(
    var userId: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var permanent: Boolean
)
