package com.urbanvoice.app.domain.model

data class RouteAssessment(
    val segments: List<SegmentAssessment>,
    val overallSafetyScore: Double
)

data class SegmentAssessment(
    val index: Int,
    val district: String,
    val riskLevel: Int,
    val riskCategory: String
)
