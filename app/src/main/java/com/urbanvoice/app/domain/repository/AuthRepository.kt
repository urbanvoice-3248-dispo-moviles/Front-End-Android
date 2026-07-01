package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun register(
        name: String,
        lastName: String,
        age: Int,
        email: String,
        phoneNumber: String,
        password: String
    ): Result<UserProfile>
    suspend fun getToken(): String?
    suspend fun getUserId(): Int?
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
}
