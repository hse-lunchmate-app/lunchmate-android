package com.example.lunchmate.domain.api

import com.example.lunchmate.domain.model.UserPatch

class ApiHelper(private val apiService: ApiService) {

    suspend fun getOffices() = apiService.getOffices()

    suspend fun getUsers(officeId: String, name: String) = apiService.getUsers(officeId, name)

    suspend fun getUser(id: String) = apiService.getUser(id)

    suspend fun patchUser(id: String, info: UserPatch) = apiService.patchUser(id, info)
}