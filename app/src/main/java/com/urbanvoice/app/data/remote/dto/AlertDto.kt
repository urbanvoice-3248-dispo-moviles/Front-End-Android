package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.urbanvoice.app.domain.model.Alert

data class AlertDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val type: String?,
    @SerializedName("alert_type") val alertType: String?,
    val title: String,
    val message: String,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String
)

fun AlertDto.toDomain() = Alert(
    id = id, userId = userId,
    type = type ?: alertType ?: "",
    title = title, message = message,
    latitude = latitude, longitude = longitude,
    isRead = isRead, createdAt = createdAt
)
