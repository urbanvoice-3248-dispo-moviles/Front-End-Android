package com.urbanvoice.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanvoice.app.domain.model.UserProfile
import com.urbanvoice.app.domain.repository.AuthRepository
import com.urbanvoice.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            authRepository.login(email, password)
                .onSuccess { profile ->
                    _state.value = _state.value.copy(
                        isLoading = false, isAuthenticated = true, profile = profile
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Credenciales inválidas"
                    )
                }
        }
    }

    fun register(
        name: String, lastName: String, age: Int, email: String,
        phoneNumber: String, password: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            authRepository.register(name, lastName, age, email, phoneNumber, password)
                .onSuccess { profile ->
                    _state.value = _state.value.copy(
                        isLoading = false, isAuthenticated = true, profile = profile
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false, error = it.message ?: "Error al registrarse"
                    )
                }
        }
    }

    fun logout() {
        _state.value = AuthState()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
