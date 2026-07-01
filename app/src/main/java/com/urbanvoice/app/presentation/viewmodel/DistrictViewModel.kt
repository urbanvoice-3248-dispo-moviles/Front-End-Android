package com.urbanvoice.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanvoice.app.domain.model.District
import com.urbanvoice.app.domain.repository.DistrictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DistrictState(
    val isLoading: Boolean = false,
    val districts: List<District> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DistrictViewModel @Inject constructor(
    private val districtRepository: DistrictRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DistrictState())
    val state: StateFlow<DistrictState> = _state.asStateFlow()

    fun getAllDistricts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            districtRepository.getAllDistricts()
                .onSuccess { districts ->
                    _state.value = _state.value.copy(isLoading = false, districts = districts)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener distritos"
                    )
                }
        }
    }
}
