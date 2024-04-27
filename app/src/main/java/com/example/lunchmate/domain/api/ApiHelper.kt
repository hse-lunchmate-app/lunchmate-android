package com.example.lunchmate.domain.api

import com.example.lunchmate.domain.model.UserPatch

class ApiHelper(private val apiService: ApiService) {

    suspend fun getOffices() = apiService.getOffices()

    suspend fun getUsers(id: String) = apiService.getUsers(id)

    suspend fun getUser(id: String) = apiService.getUser(id)

    suspend fun patchUser(id: String, info: UserPatch) = apiService.patchUser(id, info)
}