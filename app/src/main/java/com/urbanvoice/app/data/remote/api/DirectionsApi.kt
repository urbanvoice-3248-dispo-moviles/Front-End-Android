package com.urbanvoice.app.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("alternatives") alternatives: Boolean = true,
        @Query("mode") mode: String = "walking",
        @Query("key") key: String = DIRECTIONS_API_KEY
    ): DirectionsResponse
}

data class DirectionsResponse(
    val status: String,
    val routes: List<RouteDto>?
)

data class RouteDto(
    val summary: String,
    val legs: List<LegDto>?,
    @SerializedName("overview_polyline") val overviewPolyline: PolylineDto?,
    val waypointOrder: List<Int>?
)

data class LegDto(
    val distance: TextValueDto?,
    val duration: TextValueDto?,
    val steps: List<StepDto>?
)

data class TextValueDto(
    val text: String,
    val value: Int
)

data class StepDto(
    val polyline: PolylineDto?
)

data class PolylineDto(
    val points: String
)

const val DIRECTIONS_API_KEY = "AIzaSyA0yqAT0ci7jH8Xa_tesd3X_TYY33XduN8"
