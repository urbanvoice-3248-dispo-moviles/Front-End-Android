package com.urbanvoice.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanvoice.app.domain.model.UserLiveLocation
import com.urbanvoice.app.domain.repository.LocationSharingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationSharingState(
    val isLoading: Boolean = false,
    val myLocation: Pair<Double, Double>? = null,
    val friendsLocations: List<UserLiveLocation> = emptyList(),
    val myShares: List<Int> = emptyList(),
    val isSharingActive: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LocationSharingViewModel @Inject constructor(
    private val repository: LocationSharingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LocationSharingState())
    val state: StateFlow<LocationSharingState> = _state.asStateFlow()

    private var pollingJob: Job? = null

    fun startPolling(userId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                repository.getFriendsLocations(userId)
                    .onSuccess { locations ->
                        _state.value = _state.value.copy(
                            friendsLocations = locations,
                            error = null
                        )
                    }
                    .onFailure {
                        _state.value = _state.value.copy(error = it.message)
                    }
                delay(15_000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun publishLocation(userId: Int, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.publishLocation(userId, latitude, longitude)
                .onSuccess {
                    _state.value = _state.value.copy(
                        myLocation = Pair(latitude, longitude)
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al publicar ubicación"
                    )
                }
        }
    }

    fun startSharing(ownerUserId: Int, targetUserId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.startSharing(ownerUserId, targetUserId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSharingActive = true
                    )
                    loadMyShares(ownerUserId)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = it.message ?: "Error al compartir ubicación"
                    )
                }
        }
    }

    fun stopSharing(ownerUserId: Int, targetUserId: Int) {
        viewModelScope.launch {
            repository.stopSharing(ownerUserId, targetUserId)
                .onSuccess {
                    loadMyShares(ownerUserId)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al dejar de compartir"
                    )
                }
        }
    }

    fun loadMyShares(userId: Int) {
        viewModelScope.launch {
            repository.getMyShares(userId)
                .onSuccess { shares ->
                    _state.value = _state.value.copy(
                        myShares = shares,
                        isSharingActive = shares.isNotEmpty()
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al cargar comparticiones"
                    )
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
