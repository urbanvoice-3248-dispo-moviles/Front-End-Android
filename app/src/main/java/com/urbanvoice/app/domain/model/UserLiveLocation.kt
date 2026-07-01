package com.urbanvoice.app.domain.model

data class UserLiveLocation(
    val userId: Int,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: String
)
