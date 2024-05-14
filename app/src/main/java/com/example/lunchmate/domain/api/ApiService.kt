package com.example.lunchmate.domain.api

import com.example.lunchmate.domain.model.Office
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.domain.model.UserPatch
import com.google.android.gms.common.api.Response
import retrofit2.http.*
import javax.security.auth.callback.Callback

interface ApiService {

    @GET("offices")
    suspend fun getOffices(): List<Office>

    @GET("users")
    suspend fun getUsers(@Query("officeId") id: String, @Query("name") name: String): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @PATCH("users/{id}")
    suspend fun patchUser(@Path("id") id: String, @Body info: UserPatch): User

}