package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.IncidentReport

interface ReportRepository {
    suspend fun createReport(
        userId: Int,
        title: String,
        description: String,
        incidentType: String,
        latitude: Double,
        longitude: Double,
        address: String?,
        mediaUrl: String?,
        isAnonymous: Boolean
    ): Result<IncidentReport>

    suspend fun getReportById(id: Int): Result<IncidentReport>
    suspend fun getReportsByUser(userId: Int): Result<List<IncidentReport>>
    suspend fun getNearbyReports(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double
    ): Result<List<IncidentReport>>

    suspend fun updateReport(
        id: Int,
        title: String,
        description: String,
        mediaUrl: String?
    ): Result<IncidentReport>

    suspend fun getAllReports(): Result<List<IncidentReport>>

    suspend fun deleteReport(id: Int): Result<Unit>
}
