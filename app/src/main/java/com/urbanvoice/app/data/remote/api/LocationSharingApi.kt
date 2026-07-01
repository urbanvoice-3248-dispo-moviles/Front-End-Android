package com.urbanvoice.app.data.remote.api

import com.urbanvoice.app.data.remote.dto.*
import retrofit2.http.*

interface LocationSharingApi {
    @PUT("location-sharing/publish")
    suspend fun publishLocation(
        @Header("X-User-ID") userId: Int,
        @Body request: PublishLocationRequest
    ): UserLiveLocationDto

    @GET("location-sharing/friends")
    suspend fun getFriendsLocations(
        @Header("X-User-ID") userId: Int
    ): List<UserLiveLocationDto>

    @GET("location-sharing/me")
    suspend fun getMyLocation(
        @Header("X-User-ID") userId: Int
    ): UserLiveLocationDto?

    @POST("location-sharing/share")
    suspend fun startSharing(
        @Header("X-User-ID") ownerUserId: Int,
        @Body request: ShareRequest
    ): ShareSessionDto

    @DELETE("location-sharing/share/{targetUserId}")
    suspend fun stopSharing(
        @Header("X-User-ID") ownerUserId: Int,
        @Path("targetUserId") targetUserId: Int
    )

    @GET("location-sharing/shares")
    suspend fun getMyShares(
        @Header("X-User-ID") userId: Int
    ): List<ShareSessionDto>
}
