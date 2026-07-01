package com.urbanvoice.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.urbanvoice.app.data.remote.api.DirectionsResponse
import com.urbanvoice.app.data.remote.api.RouteDto
import com.urbanvoice.app.domain.repository.RouteRepository
import com.urbanvoice.app.presentation.ui.utils.decodePolyline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

data class RouteState(
    val isLoading: Boolean = false,
    val origin: Pair<Double, Double>? = null,
    val destination: Pair<Double, Double>? = null,
    val routes: List<RouteWithSafety> = emptyList(),
    val selectedRouteIndex: Int = 0,
    val error: String? = null
)

data class RouteWithSafety(
    val route: RouteDto,
    val decodedPath: List<Pair<Double, Double>>,
    val safetyScore: Double = 0.0,
    val riskSegments: List<Pair<Int, Int>> = emptyList(),
    val distance: String = "",
    val duration: String = ""
)

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RouteState())
    val state: StateFlow<RouteState> = _state.asStateFlow()

    private val directionsClient = OkHttpClient.Builder()
        .build()

    private val gson = Gson()

    fun findSafeRoute(
        originLat: Double, originLng: Double,
        destLat: Double, destLng: Double
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                origin = Pair(originLat, originLng),
                destination = Pair(destLat, destLng)
            )

            val originStr = "$originLat,$originLng"
            val destStr = "$destLat,$destLng"
            val url = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=$originStr&destination=$destStr" +
                    "&alternatives=true&mode=walking" +
                    "&key=AIzaSyA0yqAT0ci7jH8Xa_tesd3X_TYY33XduN8"

            try {
                val request = Request.Builder().url(url).build()
                val response = directionsClient.newCall(request).execute()
                val body = response.body?.string()
                val directionsResponse = gson.fromJson(body, DirectionsResponse::class.java)

                if (directionsResponse.status != "OK" || directionsResponse.routes == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No se encontraron rutas: ${directionsResponse.status}"
                    )
                    return@launch
                }

                val routesWithSafety = mutableListOf<RouteWithSafety>()
                for (route in directionsResponse.routes) {
                    val points = route.overviewPolyline?.points ?: continue
                    val decoded = decodePolyline(points)
                    val sampled = samplePoints(decoded, 10)

                    val assessment = routeRepository.assessRoute(sampled).getOrNull()
                    val dist = route.legs?.firstOrNull()?.distance?.text ?: ""
                    val dur = route.legs?.firstOrNull()?.duration?.text ?: ""

                    val riskSegments = assessment?.segments?.map {
                        Pair(it.riskLevel, it.index)
                    } ?: emptyList()

                    routesWithSafety.add(
                        RouteWithSafety(
                            route = route,
                            decodedPath = decoded,
                            safetyScore = assessment?.overallSafetyScore ?: 0.0,
                            riskSegments = riskSegments,
                            distance = dist,
                            duration = dur
                        )
                    )
                }

                routesWithSafety.sortBy { it.safetyScore }

                _state.value = _state.value.copy(
                    isLoading = false,
                    routes = routesWithSafety,
                    selectedRouteIndex = 0,
                    error = if (routesWithSafety.isEmpty()) "No se encontraron rutas" else null
                )
            } catch (e: Exception) {
                Log.e("RouteViewModel", "Error finding route", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al calcular ruta"
                )
            }
        }
    }

    fun selectRoute(index: Int) {
        _state.value = _state.value.copy(selectedRouteIndex = index)
    }

    fun setOrigin(lat: Double, lng: Double) {
        _state.value = _state.value.copy(origin = Pair(lat, lng))
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun samplePoints(points: List<Pair<Double, Double>>, maxSamples: Int): List<Pair<Double, Double>> {
        if (points.size <= maxSamples) return points
        val step = points.size / maxSamples
        return (0 until maxSamples).map { i ->
            val idx = (i * step).coerceAtMost(points.size - 1)
            points[idx]
        }
    }
}
