
package com.example.parcial2.ui.mapa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parcial2.ui.ubicaciones.UbicacionesViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.CameraUpdateFactory

@Composable
fun MapaScreen(
    onVolver: () -> Unit,
    viewModel: UbicacionesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        viewModel.cargarUbicaciones()
    }

    LaunchedEffect(state.ubicaciones) {
        val ubicaciones = state.ubicaciones

        if (ubicaciones.isNotEmpty()) {
            val puntos = ubicaciones.map {
                LatLng(it.latitud, it.longitud)
            }

            if (puntos.size == 1) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        puntos.first(),
                        15f
                    )
                )
            } else {
                val boundsBuilder =
                    com.google.android.gms.maps.model.LatLngBounds.Builder()

                puntos.forEach { punto ->
                    boundsBuilder.include(punto)
                }

                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        100
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onVolver) {
                Text("Volver")
            }

            Text(
                text = "Mapa de ubicaciones",
                style = MaterialTheme.typography.titleMedium
            )
        }

        when {
            state.cargando && state.ubicaciones.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Cargando ubicaciones...")
                }
            }

            state.ubicaciones.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.mensaje
                            ?: "Todavía no hay ubicaciones para mostrar en el mapa.",
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(onClick = { viewModel.cargarUbicaciones() }) {
                        Text("Volver a intentar")
                    }
                }
            }

            else -> {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(),
                    uiSettings = MapUiSettings(
                        mapToolbarEnabled = false
                    )
                ) {
                    state.ubicaciones.forEach { ubicacion ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    ubicacion.latitud,
                                    ubicacion.longitud
                                )
                            ),
                            title = ubicacion.nombre,
                            snippet = null,
                            onClick = {
                                viewModel.seleccionarUbicacion(ubicacion)
                                true
                            }
                        )
                    }
                }
            }
        }
    }

    state.seleccionada?.let { ubicacion ->
        AlertDialog(
            onDismissRequest = {
                viewModel.cerrarDialogo()
            },
            title = {
                Text(
                    text = ubicacion.nombre,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(
                        rememberScrollState()
                    )
                ) {
                    Text(
                        text = ubicacion.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Latitud: ${ubicacion.latitud}")
                    Text("Longitud: ${ubicacion.longitud}")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cerrarDialogo()
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}
