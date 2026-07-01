package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RouteAssessmentRequest(
    val waypoints: List<WaypointDto>
)

data class WaypointDto(
    val lat: Double,
    val lng: Double
)

data class RouteAssessmentResponse(
    val segments: List<SegmentAssessmentDto>,
    @SerializedName("overall_safety_score") val overallSafetyScore: Double
)

data class SegmentAssessmentDto(
    val index: Int,
    val district: String,
    @SerializedName("risk_level") val riskLevel: Int,
    @SerializedName("risk_category") val riskCategory: String
)
