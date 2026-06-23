package com.urbanvoice.app.domain.model

data class Location(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val district: String,
    val riskLevel: Int,
    val riskCategory: String,
    val incidentCount: Int,
    val description: String?,
    val lastUpdated: String
)
