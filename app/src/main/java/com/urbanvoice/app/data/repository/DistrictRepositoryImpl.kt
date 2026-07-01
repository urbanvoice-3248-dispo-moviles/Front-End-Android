package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.UrbanVoiceApi
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.District
import com.urbanvoice.app.domain.repository.DistrictRepository
import javax.inject.Inject

class DistrictRepositoryImpl @Inject constructor(
    private val api: UrbanVoiceApi
) : DistrictRepository {

    override suspend fun getAllDistricts(): Result<List<District>> {
        return runCatching { api.getAllDistricts().map { it.toDomain() } }
    }

    override suspend fun getDistrictById(id: Int): Result<District> {
        return runCatching { api.getDistrictById(id).toDomain() }
    }

    override suspend fun getDangerousDistricts(minRiskLevel: Int): Result<List<District>> {
        return runCatching { api.getDangerousDistricts(minRiskLevel).map { it.toDomain() } }
    }
}
