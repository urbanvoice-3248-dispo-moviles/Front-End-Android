package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.local.TokenManager
import com.urbanvoice.app.data.remote.api.UrbanVoiceApi
import com.urbanvoice.app.data.remote.dto.CreateProfileRequest
import com.urbanvoice.app.data.remote.dto.LoginRequest
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.UserProfile
import com.urbanvoice.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: UrbanVoiceApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return runCatching {
            val loginResponse = api.login(LoginRequest(email, password))
            tokenManager.saveSession(loginResponse.token, loginResponse.id, loginResponse.email)
            api.getProfileById(loginResponse.id).toDomain()
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
            val profile = api.createProfile(request).toDomain()
            // Auto-login after register to get JWT
            val loginResponse = api.login(LoginRequest(email, password))
            tokenManager.saveSession(loginResponse.token, loginResponse.id, loginResponse.email)
            profile
        }
    }

    override suspend fun getToken(): String? {
        return tokenManager.token.firstOrNull()
    }

    override suspend fun getUserId(): Int? {
        return tokenManager.userId.firstOrNull()
    }

    override suspend fun isTermsAccepted(): Boolean {
        return tokenManager.termsAccepted.firstOrNull() ?: false
    }

    override suspend fun acceptTerms() {
        tokenManager.acceptTerms()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.token.firstOrNull() != null
    }

    override suspend fun logout() {
        tokenManager.clearSession()
    }
}
