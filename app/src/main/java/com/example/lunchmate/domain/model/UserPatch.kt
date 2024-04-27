package com.example.lunchmate.domain.model

data class UserPatch(
    var name: String,
    var messenger: String,
    var tastes: String,
    var aboutMe: String,
    var officeId: Int
)