package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.UrbanVoiceApi
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.Alert
import com.urbanvoice.app.domain.repository.AlertRepository
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val api: UrbanVoiceApi
) : AlertRepository {

    override suspend fun getAllAlerts(): Result<List<Alert>> {
        return runCatching { api.getAllAlerts()?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getAlertById(id: Int): Result<Alert> {
        return runCatching { api.getAlertById(id).toDomain() }
    }

    override suspend fun getAlertsByUser(userId: Int): Result<List<Alert>> {
        return runCatching { api.getAlertsByUser(userId)?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun deleteAlert(id: Int): Result<Unit> {
        return runCatching { api.deleteAlert(id) }
    }
}
