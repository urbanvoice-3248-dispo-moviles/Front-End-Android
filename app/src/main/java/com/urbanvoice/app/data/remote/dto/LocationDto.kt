package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.urbanvoice.app.domain.model.Location

data class LocationDto(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val district: String,
    @SerializedName("risk_level") val riskLevel: Int,
    @SerializedName("risk_category") val riskCategory: String,
    @SerializedName("incident_count") val incidentCount: Int,
    val description: String?,
    @SerializedName("last_updated") val lastUpdated: String
)

fun LocationDto.toDomain() = Location(
    id = id, latitude = latitude, longitude = longitude,
    address = address, district = district, riskLevel = riskLevel,
    riskCategory = riskCategory, incidentCount = incidentCount,
    description = description, lastUpdated = lastUpdated
)
