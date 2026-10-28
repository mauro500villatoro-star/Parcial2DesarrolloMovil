
package com.example.parcial2.data.remote

import com.example.parcial2.model.ApiResponse
import com.example.parcial2.model.LoginRequest
import com.example.parcial2.model.LoginResponse
import com.example.parcial2.model.Ubicacion
import com.example.parcial2.model.UbicacionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/ubicaciones")
    suspend fun registrarUbicacion(
        @Body request: UbicacionRequest
    ): Response<ApiResponse<Ubicacion>>

    @GET("api/ubicaciones")
    suspend fun obtenerUbicaciones():
            Response<ApiResponse<List<Ubicacion>>>
}
