package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.Alert

interface AlertRepository {
    suspend fun getAllAlerts(): Result<List<Alert>>
    suspend fun getAlertById(id: Int): Result<Alert>
    suspend fun getAlertsByUser(userId: Int): Result<List<Alert>>
    suspend fun deleteAlert(id: Int): Result<Unit>
}
