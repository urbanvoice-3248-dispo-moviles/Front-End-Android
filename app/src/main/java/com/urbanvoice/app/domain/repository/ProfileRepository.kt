package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfileById(id: Int): Result<UserProfile>
    suspend fun getProfileByEmail(email: String): Result<UserProfile>
    suspend fun updateProfile(
        id: Int,
        name: String,
        lastName: String,
        age: Int,
        phoneNumber: String
    ): Result<UserProfile>
    suspend fun deleteProfile(id: Int): Result<Unit>
}
