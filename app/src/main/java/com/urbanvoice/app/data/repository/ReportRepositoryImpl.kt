package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.UrbanVoiceApi
import com.urbanvoice.app.data.remote.dto.CreateReportRequest
import com.urbanvoice.app.data.remote.dto.UpdateReportRequest
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.IncidentReport
import com.urbanvoice.app.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val api: UrbanVoiceApi
) : ReportRepository {

    override suspend fun createReport(
        userId: Int, title: String, description: String, incidentType: String,
        latitude: Double, longitude: Double, address: String?, mediaUrl: String?,
        isAnonymous: Boolean
    ): Result<IncidentReport> {
        return runCatching {
            val request = CreateReportRequest(
                title = title, description = description, incidentType = incidentType,
                latitude = latitude, longitude = longitude, address = address,
                mediaUrl = mediaUrl, isAnonymous = isAnonymous
            )
            api.createReport(userId, request).toDomain()
        }
    }

    override suspend fun getReportById(id: Int): Result<IncidentReport> {
        return runCatching { api.getReportById(id).toDomain() }
    }

    override suspend fun getReportsByUser(userId: Int): Result<List<IncidentReport>> {
        return runCatching { api.getReportsByUser(userId)?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getNearbyReports(
        latitude: Double, longitude: Double, radiusInKm: Double
    ): Result<List<IncidentReport>> {
        return runCatching { api.getNearbyReports(latitude, longitude, radiusInKm)?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun updateReport(
        id: Int, title: String, description: String, mediaUrl: String?
    ): Result<IncidentReport> {
        return runCatching {
            val request = UpdateReportRequest(title = title, description = description, mediaUrl = mediaUrl)
            api.updateReport(id, request).toDomain()
        }
    }

    override suspend fun getAllReports(): Result<List<IncidentReport>> {
        return runCatching { api.getAllReports()?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun deleteReport(id: Int): Result<Unit> {
        return runCatching { api.deleteReport(id) }
    }
}
