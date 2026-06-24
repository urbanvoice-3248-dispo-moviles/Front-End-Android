package com.urbanvoice.app.data.remote.api

import com.urbanvoice.app.data.remote.dto.*
import retrofit2.http.*

interface UrbanVoiceApi {

    // Profiles
    @POST("profiles")
    suspend fun createProfile(@Body request: CreateProfileRequest): UserProfileDto

    @GET("profiles/{id}")
    suspend fun getProfileById(@Path("id") id: Int): UserProfileDto

    @GET("profiles/email/{email}")
    suspend fun getProfileByEmail(@Path("email") email: String): UserProfileDto

    @PUT("profiles/{id}")
    suspend fun updateProfile(
        @Path("id") id: Int,
        @Body request: UpdateProfileRequest
    ): UserProfileDto

    @DELETE("profiles/{id}")
    suspend fun deleteProfile(@Path("id") id: Int)

    // Reports
    @POST("reports")
    suspend fun createReport(
        @Header("X-User-ID") userId: Int,
        @Body request: CreateReportRequest
    ): IncidentReportDto

    @GET("reports/{id}")
    suspend fun getReportById(@Path("id") id: Int): IncidentReportDto

    @GET("reports/user/{userId}")
    suspend fun getReportsByUser(@Path("userId") userId: Int): List<IncidentReportDto>?

    @GET("reports/nearby")
    suspend fun getNearbyReports(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusInKm") radiusInKm: Double = 5.0
    ): List<IncidentReportDto>?

    @PUT("reports/{id}")
    suspend fun updateReport(
        @Path("id") id: Int,
        @Body request: UpdateReportRequest
    ): IncidentReportDto

    @DELETE("reports/{id}")
    suspend fun deleteReport(@Path("id") id: Int)

    @GET("reports/all")
    suspend fun getAllReports(): List<IncidentReportDto>?

    // Locations
    @GET("locations")
    suspend fun getAllLocations(): List<LocationDto>?

    @GET("locations/{id}")
    suspend fun getLocationById(@Path("id") id: Int): LocationDto

    @GET("locations/nearby")
    suspend fun getNearbyLocations(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusInKm") radiusInKm: Double = 5.0
    ): List<LocationDto>?

    @GET("locations/district/{district}")
    suspend fun getLocationsByDistrict(@Path("district") district: String): List<LocationDto>?

    @GET("locations/dangerous")
    suspend fun getDangerousLocations(
        @Query("minRiskLevel") minRiskLevel: Int = 3
    ): List<LocationDto>?

    // Alerts
    @GET("alerts")
    suspend fun getAllAlerts(): List<AlertDto>

    @GET("alerts/{id}")
    suspend fun getAlertById(@Path("id") id: Int): AlertDto

    @GET("alerts/user/{userId}")
    suspend fun getAlertsByUser(@Path("userId") userId: Int): List<AlertDto>

    @DELETE("alerts/{id}")
    suspend fun deleteAlert(@Path("id") id: Int)
}
