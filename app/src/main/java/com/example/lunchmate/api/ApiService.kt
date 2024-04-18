package com.example.lunchmate.api

import com.example.lunchmate.model.Office
import com.example.lunchmate.model.User
import com.example.lunchmate.model.UserPatch
import com.google.android.gms.common.api.Response
import retrofit2.http.*
import javax.security.auth.callback.Callback

interface ApiService {

    @GET("offices")
    suspend fun getOffices(): List<Office>

    @GET("users")
    suspend fun getUsers(@Query("id") id: String): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @PATCH("users/{id}")
    suspend fun patchUser(@Path("id") id: String, @Body info: UserPatch): User

}