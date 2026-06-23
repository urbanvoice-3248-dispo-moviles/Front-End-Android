package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.UrbanVoiceApi
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.Location
import com.urbanvoice.app.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val api: UrbanVoiceApi
) : LocationRepository {

    override suspend fun getAllLocations(): Result<List<Location>> {
        return runCatching { api.getAllLocations().map { it.toDomain() } }
    }

    override suspend fun getLocationById(id: Int): Result<Location> {
        return runCatching { api.getLocationById(id).toDomain() }
    }

    override suspend fun getNearbyLocations(
        latitude: Double, longitude: Double, radiusInKm: Double
    ): Result<List<Location>> {
        return runCatching { api.getNearbyLocations(latitude, longitude, radiusInKm).map { it.toDomain() } }
    }

    override suspend fun getLocationsByDistrict(district: String): Result<List<Location>> {
        return runCatching { api.getLocationsByDistrict(district).map { it.toDomain() } }
    }

    override suspend fun getDangerousLocations(minRiskLevel: Int): Result<List<Location>> {
        return runCatching { api.getDangerousLocations(minRiskLevel).map { it.toDomain() } }
    }
}
