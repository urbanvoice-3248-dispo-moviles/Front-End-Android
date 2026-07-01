package com.urbanvoice.app.data.repository

import com.urbanvoice.app.data.remote.api.RouteAssessmentApi
import com.urbanvoice.app.data.remote.dto.RouteAssessmentRequest
import com.urbanvoice.app.data.remote.dto.WaypointDto
import com.urbanvoice.app.domain.model.RouteAssessment
import com.urbanvoice.app.domain.model.SegmentAssessment
import com.urbanvoice.app.domain.repository.RouteRepository
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val api: RouteAssessmentApi
) : RouteRepository {

    override suspend fun assessRoute(waypoints: List<Pair<Double, Double>>): Result<RouteAssessment> {
        return runCatching {
            val request = RouteAssessmentRequest(
                waypoints = waypoints.map { WaypointDto(it.first, it.second) }
            )
            val response = api.assessRoute(request)
            RouteAssessment(
                segments = response.segments.map {
                    SegmentAssessment(it.index, it.district, it.riskLevel, it.riskCategory)
                },
                overallSafetyScore = response.overallSafetyScore
            )
        }
    }
}
