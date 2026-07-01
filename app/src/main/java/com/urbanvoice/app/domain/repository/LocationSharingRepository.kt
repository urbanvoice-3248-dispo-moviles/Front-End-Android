package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.UserLiveLocation

interface LocationSharingRepository {
    suspend fun publishLocation(userId: Int, latitude: Double, longitude: Double): Result<UserLiveLocation>
    suspend fun getFriendsLocations(userId: Int): Result<List<UserLiveLocation>>
    suspend fun getMyLocation(userId: Int): Result<UserLiveLocation?>
    suspend fun startSharing(ownerUserId: Int, targetUserId: Int): Result<Unit>
    suspend fun stopSharing(ownerUserId: Int, targetUserId: Int): Result<Unit>
    suspend fun getMyShares(userId: Int): Result<List<Int>>
}
