package com.urbanvoice.app.data.remote

import com.urbanvoice.app.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        // Skip auth header for login and register endpoints
        if (path.contains("/auth/") || (path.contains("/profiles") && original.method == "POST")) {
            return chain.proceed(original)
        }

        return try {
            val token = runBlocking { tokenManager.token.firstOrNull() }
            
            if (token != null) {
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            } else {
                chain.proceed(original)
            }
        } catch (e: Exception) {
            // If token retrieval fails, proceed without auth header
            chain.proceed(original)
        }
    }
}
