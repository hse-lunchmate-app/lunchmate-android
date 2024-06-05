package com.example.lunchmate.domain.api

import com.example.lunchmate.domain.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

class ApiHelper(private val apiService: ApiService) {

    suspend fun getUserId(token: String) = apiService.getUserId(token)

    suspend fun getOffices(token: String) = apiService.getOffices(token)

    suspend fun getUsers(token: String, officeId: String, name: String) = apiService.getUsers(token, officeId, name)

    suspend fun getUser(token: String, id: String) = apiService.getUser(token, id)

    suspend fun patchUser(token: String, id: String, info: UserPatch) = apiService.patchUser(token, id, info)

    suspend fun getSlots(token: String, id: String) = apiService.getSlots(token, id)

    suspend fun getSlotsByDate(token: String, id: String, date: String, free: Boolean) =
        apiService.getSlotsByDate(token, id, date, free)

    suspend fun postSlot(token: String, slot: SlotPost) = apiService.postSlot(token, slot)

    suspend fun patchSlot(token: String, id: String, slot: SlotPatch) = apiService.patchSlot(token, id, slot)

    suspend fun deleteSlot(token: String, id: String) = apiService.deleteSlot(token, id)

    suspend fun getLunches(token: String, id: String, accepted: Boolean) = apiService.getLunches(token, id, accepted)

    suspend fun inviteForLunch(token: String, lunchInvitation: LunchInvitation) =
        apiService.inviteForLunch(token, lunchInvitation)

    suspend fun revokeReservation(token: String, id: String) = apiService.revokeReservation(token, id)

    suspend fun acceptInvitation(token: String, id: String) = apiService.acceptInvitation(token, id)

    suspend fun declineInvitation(token: String, id: String) = apiService.declineInvitation(token, id)
}