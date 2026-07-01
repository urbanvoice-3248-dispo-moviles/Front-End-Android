package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.urbanvoice.app.domain.model.UserLiveLocation

data class PublishLocationRequest(
    val latitude: Double,
    val longitude: Double
)

data class UserLiveLocationDto(
    @SerializedName("user_id") val userId: Int,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("updated_at") val updatedAt: String
)

data class ShareRequest(
    @SerializedName("target_user_id") val targetUserId: Int
)

data class ShareSessionDto(
    val id: Int,
    @SerializedName("owner_user_id") val ownerUserId: Int,
    @SerializedName("target_user_id") val targetUserId: Int,
    val active: Boolean,
    @SerializedName("created_at") val createdAt: String
)

fun UserLiveLocationDto.toDomain() = UserLiveLocation(
    userId = userId, latitude = latitude,
    longitude = longitude, updatedAt = updatedAt
)
