package com.example.lunchmate.domain.api

import com.example.lunchmate.domain.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("offices")
    suspend fun getOffices(): List<Office>

    @GET("users")
    suspend fun getUsers(@Query("officeId") id: String, @Query("name") name: String): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @PATCH("users/{id}")
    suspend fun patchUser(@Path("id") id: String, @Body info: UserPatch): User

    @GET("timetable/{userId}")
    suspend fun getSlots(@Path("userId") id: String): List<SlotTimetable>

    @GET("timetable/{userId}")
    suspend fun getSlotsByDate(
        @Path("userId") id: String,
        @Query("date") date: String,
        @Query("free") free: Boolean
    ): List<SlotTimetable>

    @POST("timetable/slot")
    suspend fun postSlot(@Body slot: SlotPost): SlotTimetable

    @PATCH("timetable/slot/{id}")
    suspend fun patchSlot(@Path("id") id: String, @Body slot: SlotPatch): SlotTimetable

    @DELETE("timetable/slot/{id}")
    suspend fun deleteSlot(@Path("id") id: String): Response<Unit>

    @GET("lunches/{userId}")
    suspend fun getLunches(
        @Path("userId") id: String,
        @Query("accepted") accepted: Boolean
    ): List<Lunch>

    @POST("lunches/invite")
    suspend fun inviteForLunch(@Body lunchInvitation: LunchInvitation): Lunch

    @DELETE("lunches/{id}/revoke")
    suspend fun revokeReservation(@Path("id") id: String): Response<Unit>

    @POST("lunches/{id}/accept")
    suspend fun acceptInvitation(@Path("id") id: String): Response<Unit>

    @DELETE("lunches/{id}/decline")
    suspend fun declineInvitation(@Path("id") id: String): Response<Unit>
}