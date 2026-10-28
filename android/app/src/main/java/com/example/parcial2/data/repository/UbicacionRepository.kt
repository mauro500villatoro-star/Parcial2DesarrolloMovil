
package com.example.parcial2.data.repository

import com.example.parcial2.data.remote.RetrofitClient
import com.example.parcial2.model.Ubicacion
import com.example.parcial2.model.UbicacionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UbicacionRepository {

    suspend fun obtenerUbicaciones(): Result<List<Ubicacion>> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    RetrofitClient.apiService.obtenerUbicaciones()

                if (!response.isSuccessful) {
                    val mensaje = when (response.code()) {
                        401 -> "Sesión vencida o token no válido. Inicia sesión nuevamente."
                        404 -> "No se encontró la ruta de ubicaciones."
                        else -> "Error al consultar ubicaciones: ${response.code()}"
                    }
                    return@withContext Result.failure(Exception(mensaje))
                }

                val cuerpo = response.body()

                if (cuerpo?.success != true || cuerpo.data == null) {
                    return@withContext Result.failure(
                        Exception(cuerpo?.message ?: "No se recibieron las ubicaciones.")
                    )
                }

                Result.success(cuerpo.data)
            } catch (e: Exception) {
                Result.failure(
                    Exception(e.message ?: "Error de conexión con el servidor.")
                )
            }
        }

    suspend fun registrarUbicacion(
        request: UbicacionRequest
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response =
                RetrofitClient.apiService.registrarUbicacion(request)

            if (!response.isSuccessful) {
                val mensaje = when (response.code()) {
                    400 -> "Los datos de la ubicación no son válidos."
                    401 -> "Sesión vencida o token no válido. Inicia sesión nuevamente."
                    404 -> "No se encontró la ruta para registrar ubicaciones."
                    else -> "Error al guardar ubicación: ${response.code()}"
                }
                return@withContext Result.failure(Exception(mensaje))
            }

            val cuerpo = response.body()

            if (cuerpo?.success != true) {
                return@withContext Result.failure(
                    Exception(cuerpo?.message ?: "No se pudo guardar la ubicación.")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                Exception(e.message ?: "Error de conexión con el servidor.")
            )
        }
    }
}
