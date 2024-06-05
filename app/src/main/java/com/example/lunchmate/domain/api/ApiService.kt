package com.example.lunchmate.domain.api

import com.example.lunchmate.domain.model.*
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @GET("api/authenticated")
    suspend fun getUserId(@Header("Authorization") token: String): UserId

    @GET("api/offices")
    suspend fun getOffices(@Header("Authorization") token: String): List<Office>

    @GET("api/users")
    suspend fun getUsers(@Header("Authorization") token: String, @Query("officeId") id: String, @Query("name") name: String): List<User>

    @GET("api/users/{id}")
    suspend fun getUser(@Header("Authorization") token: String, @Path("id") id: String): User

    @PATCH("api/users/{id}")
    suspend fun patchUser(@Header("Authorization") token: String, @Path("id") id: String, @Body info: UserPatch): User

    @GET("api/timetable/{userId}")
    suspend fun getSlots(@Header("Authorization") token: String, @Path("userId") id: String): List<SlotTimetable>

    @GET("api/timetable/{userId}")
    suspend fun getSlotsByDate(
        @Header("Authorization") token: String,
        @Path("userId") id: String,
        @Query("date") date: String,
        @Query("free") free: Boolean
    ): List<SlotTimetable>

    @POST("api/timetable/slot")
    suspend fun postSlot(@Header("Authorization") token: String, @Body slot: SlotPost): SlotTimetable

    @PATCH("api/timetable/slot/{id}")
    suspend fun patchSlot(@Header("Authorization") token: String, @Path("id") id: String, @Body slot: SlotPatch): SlotTimetable

    @DELETE("api/timetable/slot/{id}")
    suspend fun deleteSlot(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>

    @GET("api/lunches/{userId}")
    suspend fun getLunches(
        @Header("Authorization") token: String,
        @Path("userId") id: String,
        @Query("accepted") accepted: Boolean
    ): List<Lunch>

    @POST("api/lunches/invite")
    suspend fun inviteForLunch(@Header("Authorization") token: String, @Body lunchInvitation: LunchInvitation): Lunch

    @DELETE("api/lunches/{id}/revoke")
    suspend fun revokeReservation(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>

    @POST("api/lunches/{id}/accept")
    suspend fun acceptInvitation(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>

    @DELETE("api/lunches/{id}/decline")
    suspend fun declineInvitation(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>
}