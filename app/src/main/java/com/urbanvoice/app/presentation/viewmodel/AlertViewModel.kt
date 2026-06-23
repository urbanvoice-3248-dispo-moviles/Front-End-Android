package com.urbanvoice.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanvoice.app.domain.model.Alert
import com.urbanvoice.app.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertState(
    val isLoading: Boolean = false,
    val alerts: List<Alert> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlertState())
    val state: StateFlow<AlertState> = _state.asStateFlow()

    fun getAllAlerts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            alertRepository.getAllAlerts()
                .onSuccess { alerts ->
                    _state.value = _state.value.copy(isLoading = false, alerts = alerts)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener alertas"
                    )
                }
        }
    }

    fun getAlertsByUser(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            alertRepository.getAlertsByUser(userId)
                .onSuccess { alerts ->
                    _state.value = _state.value.copy(isLoading = false, alerts = alerts)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener alertas"
                    )
                }
        }
    }
}
