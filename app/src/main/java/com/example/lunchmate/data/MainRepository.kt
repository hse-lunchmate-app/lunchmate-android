package com.example.lunchmate.data

import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.model.LunchInvitation
import com.example.lunchmate.domain.model.SlotPatch
import com.example.lunchmate.domain.model.SlotPost
import com.example.lunchmate.domain.model.UserPatch

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getUserId(token: String) = apiHelper.getUserId(token)

    suspend fun getOffices(token: String) = apiHelper.getOffices(token)

    suspend fun getUsers(token: String, officeId: String, name: String) = apiHelper.getUsers(token, officeId, name)

    suspend fun getUser(token: String, id: String) = apiHelper.getUser(token, id)

    suspend fun patchUser(token: String, id: String, info: UserPatch) = apiHelper.patchUser(token, id, info)

    suspend fun getSlots(token: String, id: String) = apiHelper.getSlots(token, id)

    suspend fun getSlotsByDate(token: String, id: String, date: String, free: Boolean) = apiHelper.getSlotsByDate(token, id, date, free)

    suspend fun postSlot(token: String, slot: SlotPost) = apiHelper.postSlot(token, slot)

    suspend fun patchSlot(token: String, id: String, slot: SlotPatch) = apiHelper.patchSlot(token, id, slot)

    suspend fun deleteSlot(token: String, id: String) = apiHelper.deleteSlot(token, id)

    suspend fun getLunches(token: String, id: String, accepted: Boolean) = apiHelper.getLunches(token, id, accepted)

    suspend fun inviteForLunch(token: String, lunchInvitation: LunchInvitation) = apiHelper.inviteForLunch(token, lunchInvitation)

    suspend fun revokeReservation(token: String, id: String) = apiHelper.revokeReservation(token, id)

    suspend fun acceptInvitation(token: String, id: String) = apiHelper.acceptInvitation(token, id)

    suspend fun declineInvitation(token: String, id: String) = apiHelper.declineInvitation(token, id)
}