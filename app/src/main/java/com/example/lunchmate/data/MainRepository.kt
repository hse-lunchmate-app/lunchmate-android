package com.example.lunchmate.data

import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.model.UserPatch

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getOffices() = apiHelper.getOffices()

    suspend fun getUsers(officeId: String, name: String) = apiHelper.getUsers(officeId, name)

    suspend fun getUser(id: String) = apiHelper.getUser(id)

    suspend fun patchUser(id: String, info: UserPatch) = apiHelper.patchUser(id, info)
}