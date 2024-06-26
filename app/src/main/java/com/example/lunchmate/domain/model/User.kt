package com.example.lunchmate.domain.model

data class User(
    val id: String,
    val login: String,
    var name: String,
    var messenger: String,
    var tastes: String,
    var aboutMe: String,
    var office: Office
)