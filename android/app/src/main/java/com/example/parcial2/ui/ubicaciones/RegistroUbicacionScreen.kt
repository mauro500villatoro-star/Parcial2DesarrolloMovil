
package com.example.parcial2.ui.ubicaciones

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun RegistroUbicacionScreen(
    onVolver: () -> Unit,
    viewModel: UbicacionesViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mensajeGps by remember { mutableStateOf<String?>(null) }

    // Solicita los permisos de ubicación cuando todavía no están concedidos.
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (concedido) {
            obtenerUbicacionActual(
                context = context,
                onUbicacion = { latitud, longitud ->
                    viewModel.registrar(
                        nombre = nombre,
                        descripcion = descripcion,
                        latitud = latitud,
                        longitud = longitud
                    )
                },
                onError = { mensaje ->
                    mensajeGps = mensaje
                }
            )
        } else {
            mensajeGps =
                "Debes conceder permiso de ubicación para capturar el GPS."
        }
    }

    // Comprueba el permiso y obtiene la ubicación actual.
    fun capturarYGuardar() {
        if (nombre.isBlank() || descripcion.isBlank()) {
            mensajeGps = "Completa el nombre y la descripción."
            return
        }

        val tieneUbicacionPrecisa =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val tieneUbicacionAproximada =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (tieneUbicacionPrecisa || tieneUbicacionAproximada) {
            mensajeGps = null

            obtenerUbicacionActual(
                context = context,
                onUbicacion = { latitud, longitud ->
                    viewModel.registrar(
                        nombre = nombre,
                        descripcion = descripcion,
                        latitud = latitud,
                        longitud = longitud
                    )
                },
                onError = { mensaje ->
                    mensajeGps = mensaje
                }
            )
        } else {
            permisoLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registrar ubicación",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        mensajeGps?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        state.mensaje?.let { mensaje ->
            Text(text = mensaje)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.cargando) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = { capturarYGuardar() },
            enabled = !state.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capturar GPS y guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

@SuppressLint("MissingPermission")
private fun obtenerUbicacionActual(
    context: android.content.Context,
    onUbicacion: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    val cliente =
        LocationServices.getFusedLocationProviderClient(context)

    cliente.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { ubicacion ->
        if (ubicacion != null) {
            onUbicacion(
                ubicacion.latitude,
                ubicacion.longitude
            )
        } else {
            onError(
                "No se pudo obtener el GPS. Activa la ubicación del teléfono e inténtalo de nuevo."
            )
        }
    }.addOnFailureListener {
        onError("Ocurrió un error al obtener la ubicación GPS.")
    }
}
