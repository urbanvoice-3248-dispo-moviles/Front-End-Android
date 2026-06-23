package com.urbanvoice.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanvoice.app.domain.model.Location
import com.urbanvoice.app.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationState(
    val isLoading: Boolean = false,
    val locations: List<Location> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LocationState())
    val state: StateFlow<LocationState> = _state.asStateFlow()

    fun getAllLocations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            locationRepository.getAllLocations()
                .onSuccess { locations ->
                    _state.value = _state.value.copy(isLoading = false, locations = locations)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener ubicaciones"
                    )
                }
        }
    }

    fun getNearbyLocations(latitude: Double, longitude: Double, radiusInKm: Double = 5.0) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            locationRepository.getNearbyLocations(latitude, longitude, radiusInKm)
                .onSuccess { locations ->
                    _state.value = _state.value.copy(isLoading = false, locations = locations)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener ubicaciones"
                    )
                }
        }
    }
}
