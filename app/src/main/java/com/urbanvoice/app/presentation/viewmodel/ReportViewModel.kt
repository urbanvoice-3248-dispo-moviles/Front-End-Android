package com.urbanvoice.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanvoice.app.domain.model.IncidentReport
import com.urbanvoice.app.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportState(
    val isLoading: Boolean = false,
    val reports: List<IncidentReport> = emptyList(),
    val selectedReport: IncidentReport? = null,
    val isCreated: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    fun createReport(
        userId: Int, title: String, description: String, incidentType: String,
        latitude: Double, longitude: Double, address: String?, mediaUrl: String?,
        isAnonymous: Boolean
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            reportRepository.createReport(
                userId, title, description, incidentType,
                latitude, longitude, address, mediaUrl, isAnonymous
            )
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false, isCreated = true)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al crear reporte"
                    )
                }
        }
    }

    fun getReportsByUser(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            reportRepository.getReportsByUser(userId)
                .onSuccess { reports ->
                    _state.value = _state.value.copy(isLoading = false, reports = reports)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener reportes"
                    )
                }
        }
    }

    fun getNearbyReports(latitude: Double, longitude: Double, radiusInKm: Double = 5.0) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            reportRepository.getNearbyReports(latitude, longitude, radiusInKm)
                .onSuccess { reports ->
                    _state.value = _state.value.copy(isLoading = false, reports = reports)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener reportes"
                    )
                }
        }
    }

    fun getReportById(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            reportRepository.getReportById(id)
                .onSuccess { report ->
                    _state.value = _state.value.copy(isLoading = false, selectedReport = report)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener reporte"
                    )
                }
        }
    }

    fun deleteReport(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            reportRepository.deleteReport(id)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false, isDeleted = true)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al eliminar reporte"
                    )
                }
        }
    }

    fun clearState() {
        _state.value = ReportState()
    }
}
