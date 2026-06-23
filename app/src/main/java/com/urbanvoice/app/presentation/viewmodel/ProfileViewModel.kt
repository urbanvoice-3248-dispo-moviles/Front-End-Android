package com.urbanvoice.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanvoice.app.domain.model.UserProfile
import com.urbanvoice.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun getProfileById(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            profileRepository.getProfileById(id)
                .onSuccess { profile ->
                    _state.value = _state.value.copy(isLoading = false, profile = profile)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al obtener perfil"
                    )
                }
        }
    }

    fun updateProfile(
        id: Int, name: String, lastName: String, age: Int, phoneNumber: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            profileRepository.updateProfile(id, name, lastName, age, phoneNumber)
                .onSuccess { profile ->
                    _state.value = _state.value.copy(isLoading = false, profile = profile)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al actualizar perfil"
                    )
                }
        }
    }
}
