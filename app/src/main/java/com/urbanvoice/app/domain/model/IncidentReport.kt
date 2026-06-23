package com.urbanvoice.app.domain.model

data class IncidentReport(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val incidentType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val mediaUrl: String?,
    val isAnonymous: Boolean,
    val reportedAt: String
)
