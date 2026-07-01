package com.urbanvoice.app.data.remote.api

import com.urbanvoice.app.data.remote.dto.RouteAssessmentRequest
import com.urbanvoice.app.data.remote.dto.RouteAssessmentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RouteAssessmentApi {
    @POST("routes/assess")
    suspend fun assessRoute(
        @Body request: RouteAssessmentRequest
    ): RouteAssessmentResponse
}
