package com.example.lunchmate.model

data class UserPatch(
    var name: String,
    var messenger: String,
    var tastes: String,
    var aboutMe: String,
    var officeId: Int
)