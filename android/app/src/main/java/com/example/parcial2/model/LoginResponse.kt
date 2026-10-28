package com.example.parcial2.model

data class LoginResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: LoginData? = null
)

data class LoginData(
    val token: String? = null,
    val user: LoginUser? = null
)

data class LoginUser(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val usuario: String,
    val correo: String,
    val rol: String
)
