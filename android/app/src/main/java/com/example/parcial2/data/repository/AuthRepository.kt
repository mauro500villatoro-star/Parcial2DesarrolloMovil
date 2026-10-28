package com.example.parcial2.data.repository

import com.example.parcial2.data.local.TokenManager
import com.example.parcial2.data.remote.RetrofitClient
import com.example.parcial2.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    suspend fun login(
        usuario: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService.login(
                LoginRequest(
                    usuario = usuario.trim(),
                    password = password
                )
            )

            if (!response.isSuccessful) {
                val mensaje = when (response.code()) {
                    401 -> "Usuario o contraseña incorrectos."
                    404 -> "No se encontró la ruta de inicio de sesión."
                    500 -> "Error interno del servidor."
                    else -> "Error del servidor: ${response.code()}"
                }

                return@withContext Result.failure(
                    Exception(mensaje)
                )
            }

            val cuerpo = response.body()
            val token = cuerpo?.data?.token

            if (cuerpo?.success != true || token.isNullOrBlank()) {
                return@withContext Result.failure(
                    Exception(
                        cuerpo?.message
                            ?: "El servidor no devolvió un token válido."
                    )
                )
            }

            TokenManager.guardarToken(token)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message
                        ?: "No se pudo conectar con el servidor."
                )
            )
        }
    }

    fun cerrarSesion() {
        TokenManager.cerrarSesion()
    }
}
