package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.LocationSharingApi
import com.urbanvoice.app.data.remote.dto.PublishLocationRequest
import com.urbanvoice.app.data.remote.dto.ShareRequest
import com.urbanvoice.app.data.remote.dto.toDomain
import com.urbanvoice.app.domain.model.UserLiveLocation
import com.urbanvoice.app.domain.repository.LocationSharingRepository
import javax.inject.Inject

class LocationSharingRepositoryImpl @Inject constructor(
    private val api: LocationSharingApi
) : LocationSharingRepository {

    override suspend fun publishLocation(
        userId: Int, latitude: Double, longitude: Double
    ): Result<UserLiveLocation> {
        return runCatching {
            api.publishLocation(userId, PublishLocationRequest(latitude, longitude)).toDomain()
        }
    }

    override suspend fun getFriendsLocations(userId: Int): Result<List<UserLiveLocation>> {
        return runCatching {
            api.getFriendsLocations(userId).map { it.toDomain() }
        }
    }

    override suspend fun getMyLocation(userId: Int): Result<UserLiveLocation?> {
        return runCatching { api.getMyLocation(userId)?.toDomain() }
    }

    override suspend fun startSharing(ownerUserId: Int, targetUserId: Int): Result<Unit> {
        return runCatching {
            api.startSharing(ownerUserId, ShareRequest(targetUserId))
            Unit
        }
    }

    override suspend fun stopSharing(ownerUserId: Int, targetUserId: Int): Result<Unit> {
        return runCatching {
            api.stopSharing(ownerUserId, targetUserId)
            Unit
        }
    }

    override suspend fun getMyShares(userId: Int): Result<List<Int>> {
        return runCatching {
            api.getMyShares(userId).map { it.targetUserId }
        }
    }
}
