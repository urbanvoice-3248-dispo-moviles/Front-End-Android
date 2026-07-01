package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.urbanvoice.app.domain.model.Location

data class LocationDto(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val district: String,
    val description: String?,
    @SerializedName("created_at") val createdAt: String
)

fun LocationDto.toDomain() = Location(
    id = id, latitude = latitude, longitude = longitude,
    address = address, district = district,
    description = description, createdAt = createdAt
)
