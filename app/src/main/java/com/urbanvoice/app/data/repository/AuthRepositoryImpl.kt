package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.UrbanVoiceApi
import com.urbanvoice.app.data.remote.dto.CreateProfileRequest
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.UserProfile
import com.urbanvoice.app.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: UrbanVoiceApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return runCatching {
            api.getProfileByEmail(email).toDomain()
        }
    }

    override suspend fun register(
        name: String, lastName: String, age: Int, email: String,
        phoneNumber: String, password: String
    ): Result<UserProfile> {
        return runCatching {
            val request = CreateProfileRequest(
                name = name, lastName = lastName, age = age,
                email = email, phoneNumber = phoneNumber, password = password
            )
            api.createProfile(request).toDomain()
        }
    }
}
