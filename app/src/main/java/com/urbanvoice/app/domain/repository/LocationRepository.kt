package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.Location

interface LocationRepository {
    suspend fun getAllLocations(): Result<List<Location>>
    suspend fun getLocationById(id: Int): Result<Location>
    suspend fun getNearbyLocations(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double
    ): Result<List<Location>>

    suspend fun getLocationsByDistrict(district: String): Result<List<Location>>
    suspend fun getDangerousLocations(minRiskLevel: Int): Result<List<Location>>
}
