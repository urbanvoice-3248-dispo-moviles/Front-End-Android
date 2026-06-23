package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.UrbanVoiceApi
import com.urbanvoice.app.data.remote.dto.UpdateProfileRequest
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.UserProfile
import com.urbanvoice.app.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: UrbanVoiceApi
) : ProfileRepository {

    override suspend fun getProfileById(id: Int): Result<UserProfile> {
        return runCatching { api.getProfileById(id).toDomain() }
    }

    override suspend fun getProfileByEmail(email: String): Result<UserProfile> {
        return runCatching { api.getProfileByEmail(email).toDomain() }
    }

    override suspend fun updateProfile(
        id: Int, name: String, lastName: String, age: Int, phoneNumber: String
    ): Result<UserProfile> {
        return runCatching {
            val request = UpdateProfileRequest(
                name = name, lastName = lastName, age = age, phoneNumber = phoneNumber
            )
            api.updateProfile(id, request).toDomain()
        }
    }

    override suspend fun deleteProfile(id: Int): Result<Unit> {
        return runCatching { api.deleteProfile(id) }
    }
}
