package com.urbanvoice.app.domain.repository

import com.urbanvoice.app.domain.model.RouteAssessment

interface RouteRepository {
    suspend fun assessRoute(waypoints: List<Pair<Double, Double>>): Result<RouteAssessment>
}
