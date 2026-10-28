
package com.example.parcial2.model

import com.google.gson.annotations.SerializedName

data class Ubicacion(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    @SerializedName("usuarioId")
    val usuarioId: Int? = null,
    @SerializedName("creadoEn")
    val creadoEn: String? = null
)
