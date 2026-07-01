package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.District

interface DistrictRepository {
    suspend fun getAllDistricts(): Result<List<District>>
    suspend fun getDistrictById(id: Int): Result<District>
    suspend fun getDangerousDistricts(minRiskLevel: Int): Result<List<District>>
}
