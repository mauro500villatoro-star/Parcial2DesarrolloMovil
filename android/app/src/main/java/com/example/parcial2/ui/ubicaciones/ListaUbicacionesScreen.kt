package com.example.parcial2.ui.ubicaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ListaUbicacionesScreen(
    onVolver: () -> Unit,
    viewModel: UbicacionesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarUbicaciones()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis ubicaciones",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = { viewModel.cargarUbicaciones() },
            enabled = !state.cargando
        ) {
            Text("Actualizar")
        }

        Button(onClick = onVolver) {
            Text("Volver")
        }

        state.mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (state.cargando) {
            CircularProgressIndicator()
        }

        if (!state.cargando && state.ubicaciones.isEmpty()) {
            Text("Todavía no tienes ubicaciones guardadas.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = state.ubicaciones,
                key = { it.id }
            ) { ubicacion ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = ubicacion.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(ubicacion.descripcion)
                        Text("Latitud: ${ubicacion.latitud}")
                        Text("Longitud: ${ubicacion.longitud}")
                        ubicacion.creadoEn?.let {
                            Text("Fecha: $it")
                        }
                    }
                }
            }
        }
    }
}

