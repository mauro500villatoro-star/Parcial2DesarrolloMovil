package com.example.parcial2.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onRegistrar: () -> Unit,
    onListado: () -> Unit,
    onMapa: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "GeoTracker",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(8.dp))

        Text("Administra tus ubicaciones de interés")

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onRegistrar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar ubicación GPS")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onListado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis ubicaciones")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onMapa,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver mapa")
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
