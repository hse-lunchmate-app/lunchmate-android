package com.example.lunchmate.domain.api

import com.example.lunchmate.domain.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

class ApiHelper(private val apiService: ApiService) {

    suspend fun getOffices() = apiService.getOffices()

    suspend fun getUsers(officeId: String, name: String) = apiService.getUsers(officeId, name)

    suspend fun getUser(id: String) = apiService.getUser(id)

    suspend fun patchUser(id: String, info: UserPatch) = apiService.patchUser(id, info)

    suspend fun getSlots(id: String) = apiService.getSlots(id)

    suspend fun getSlotsByDate(id: String, date: String, free: Boolean) =
        apiService.getSlotsByDate(id, date, free)

    suspend fun postSlot(slot: SlotPost) = apiService.postSlot(slot)

    suspend fun patchSlot(id: String, slot: SlotPatch) = apiService.patchSlot(id, slot)

    suspend fun deleteSlot(id: String) = apiService.deleteSlot(id)

    suspend fun getLunches(id: String, accepted: Boolean) = apiService.getLunches(id, accepted)

    suspend fun inviteForLunch(lunchInvitation: LunchInvitation) =
        apiService.inviteForLunch(lunchInvitation)

    suspend fun revokeReservation(id: String) = apiService.revokeReservation(id)

    suspend fun acceptInvitation(id: String) = apiService.acceptInvitation(id)

    suspend fun declineInvitation(id: String) = apiService.declineInvitation(id)
}