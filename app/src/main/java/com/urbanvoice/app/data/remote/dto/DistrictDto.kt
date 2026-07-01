package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.urbanvoice.app.domain.model.District
import com.urbanvoice.app.domain.model.GeoPoint

data class DistrictDto(
    val id: Int,
    val name: String,
    @SerializedName("risk_level") val riskLevel: Int,
    @SerializedName("risk_category") val riskCategory: String,
    @SerializedName("risk_description") val riskDescription: String?,
    val boundary: List<BoundaryPointDto>?,
    val description: String?,
    @SerializedName("incident_count") val incidentCount: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class BoundaryPointDto(
    val latitude: Double,
    val longitude: Double
)

fun DistrictDto.toDomain() = District(
    id = id, name = name, riskLevel = riskLevel,
    riskCategory = riskCategory, riskDescription = riskDescription,
    boundary = boundary?.map { GeoPoint(it.latitude, it.longitude) } ?: emptyList(),
    description = description, incidentCount = incidentCount,
    createdAt = createdAt, updatedAt = updatedAt
)
