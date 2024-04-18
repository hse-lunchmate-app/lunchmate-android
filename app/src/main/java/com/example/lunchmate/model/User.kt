package com.example.lunchmate.model

data class User(
    val id: Int,
    val login: String,
    var name: String,
    var messenger: String,
    var tastes: String,
    var aboutMe: String,
    var office: Office
)