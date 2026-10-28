
package com.example.parcial2.ui.ubicaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.repository.UbicacionRepository
import com.example.parcial2.model.Ubicacion
import com.example.parcial2.model.UbicacionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UbicacionesUiState(
    val ubicaciones: List<Ubicacion> = emptyList(),
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val seleccionada: Ubicacion? = null
)

class UbicacionesViewModel : ViewModel() {

    private val repository = UbicacionRepository()

    private val _uiState = MutableStateFlow(UbicacionesUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarUbicaciones() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensaje = null
            )

            val resultado = repository.obtenerUbicaciones()

            _uiState.value = if (resultado.isSuccess) {
                _uiState.value.copy(
                    ubicaciones = resultado.getOrDefault(emptyList()),
                    cargando = false,
                    mensaje = null
                )
            } else {
                _uiState.value.copy(
                    cargando = false,
                    mensaje = resultado.exceptionOrNull()?.message
                        ?: "No se pudieron cargar las ubicaciones."
                )
            }
        }
    }

    fun registrar(
        nombre: String,
        descripcion: String,
        latitud: Double,
        longitud: Double
    ) {
        if (nombre.isBlank() || descripcion.isBlank()) {
            _uiState.value = _uiState.value.copy(
                mensaje = "Completa el nombre y la descripción."
            )
            return
        }

        if (!latitud.isFinite() || latitud !in -90.0..90.0 ||
            !longitud.isFinite() || longitud !in -180.0..180.0
        ) {
            _uiState.value = _uiState.value.copy(
                mensaje = "Las coordenadas GPS no son válidas."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensaje = null
            )

            val resultado = repository.registrarUbicacion(
                UbicacionRequest(
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim(),
                    latitud = latitud,
                    longitud = longitud
                )
            )

            if (resultado.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensaje = "Ubicación guardada correctamente."
                )

                cargarUbicaciones()
            } else {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensaje = resultado.exceptionOrNull()?.message
                        ?: "No se pudo guardar la ubicación."
                )
            }
        }
    }

    fun seleccionarUbicacion(ubicacion: Ubicacion) {
        _uiState.value = _uiState.value.copy(
            seleccionada = ubicacion
        )
    }

    fun cerrarDialogo() {
        _uiState.value = _uiState.value.copy(
            seleccionada = null
        )
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(
            mensaje = null
        )
    }
}
