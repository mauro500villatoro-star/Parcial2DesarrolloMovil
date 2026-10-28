package com.example.parcial2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val autenticado: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarSesion(usuario: String, password: String) {
        if (usuario.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(
                error = "Completa el usuario y la contraseña"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(cargando = true)

            val resultado = repository.login(usuario, password)

            _uiState.value = if (resultado.isSuccess) {
                LoginUiState(autenticado = true)
            } else {
                LoginUiState(
                    error = resultado.exceptionOrNull()?.message
                        ?: "No fue posible iniciar sesión"
                )
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun consumirAutenticacion() {
        _uiState.value = _uiState.value.copy(autenticado = false)
    }
}
