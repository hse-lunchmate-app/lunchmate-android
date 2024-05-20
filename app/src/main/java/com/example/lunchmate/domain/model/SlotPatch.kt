package com.example.lunchmate.domain.model

data class SlotPatch(
    var date: String,
    var startTime: String,
    var endTime: String,
    var permanent: Boolean
)
