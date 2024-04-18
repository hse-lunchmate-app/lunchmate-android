package com.example.lunchmate.repository

import com.example.lunchmate.api.ApiHelper
import com.example.lunchmate.model.UserPatch

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getOffices() = apiHelper.getOffices()

    suspend fun getUsers(id: String) = apiHelper.getUsers(id)

    suspend fun getUser(id: String) = apiHelper.getUser(id)

    suspend fun patchUser(id: String, info: UserPatch) = apiHelper.patchUser(id, info)
}