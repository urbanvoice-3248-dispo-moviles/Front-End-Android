package com.urbanvoice.app.domain.model

data class Location(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val district: String,
    val description: String?,
    val createdAt: String
)
