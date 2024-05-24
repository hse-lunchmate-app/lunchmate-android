package com.example.lunchmate.data

import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.model.LunchInvitation
import com.example.lunchmate.domain.model.SlotPatch
import com.example.lunchmate.domain.model.SlotPost
import com.example.lunchmate.domain.model.UserPatch

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getOffices() = apiHelper.getOffices()

    suspend fun getUsers(officeId: String, name: String) = apiHelper.getUsers(officeId, name)

    suspend fun getUser(id: String) = apiHelper.getUser(id)

    suspend fun patchUser(id: String, info: UserPatch) = apiHelper.patchUser(id, info)

    suspend fun getSlots(id: String) = apiHelper.getSlots(id)

    suspend fun getSlotsByDate(id: String, date: String, free: Boolean) = apiHelper.getSlotsByDate(id, date, free)

    suspend fun postSlot(slot: SlotPost) = apiHelper.postSlot(slot)

    suspend fun patchSlot(id: String, slot: SlotPatch) = apiHelper.patchSlot(id, slot)

    suspend fun deleteSlot(id: String) = apiHelper.deleteSlot(id)

    suspend fun getLunches(id: String, accepted: Boolean) = apiHelper.getLunches(id, accepted)

    suspend fun inviteForLunch(lunchInvitation: LunchInvitation) = apiHelper.inviteForLunch(lunchInvitation)

    suspend fun revokeReservation(id: String) = apiHelper.revokeReservation(id)

    suspend fun acceptInvitation(id: String) = apiHelper.acceptInvitation(id)

    suspend fun declineInvitation(id: String) = apiHelper.declineInvitation(id)
}