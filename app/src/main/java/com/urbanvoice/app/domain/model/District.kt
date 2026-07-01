package com.urbanvoice.app.domain.model

data class District(
    val id: Int,
    val name: String,
    val riskLevel: Int,
    val riskCategory: String,
    val riskDescription: String?,
    val boundary: List<GeoPoint>,
    val description: String?,
    val incidentCount: Int,
    val createdAt: String,
    val updatedAt: String
)
