package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.urbanvoice.app.domain.model.IncidentReport

data class IncidentReportDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val title: String,
    val description: String,
    @SerializedName("incident_type") val incidentType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    @SerializedName("media_url") val mediaUrl: String?,
    @SerializedName("is_anonymous") val isAnonymous: Boolean,
    @SerializedName("reported_at") val reportedAt: String
)

fun IncidentReportDto.toDomain() = IncidentReport(
    id = id, userId = userId, title = title, description = description,
    incidentType = incidentType, latitude = latitude, longitude = longitude,
    address = address, mediaUrl = mediaUrl, isAnonymous = isAnonymous,
    reportedAt = reportedAt
)

data class CreateReportRequest(
    val title: String,
    val description: String,
    @SerializedName("incident_type") val incidentType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    @SerializedName("media_url") val mediaUrl: String?,
    @SerializedName("is_anonymous") val isAnonymous: Boolean = false
)

data class UpdateReportRequest(
    val title: String,
    val description: String,
    @SerializedName("media_url") val mediaUrl: String? = null
)
