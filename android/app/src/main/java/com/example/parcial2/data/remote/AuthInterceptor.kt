package com.example.parcial2.data.remote

import com.example.parcial2.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // El login no necesita un token previo.
        if (request.url.encodedPath.endsWith("/api/login")) {
            return chain.proceed(request)
        }

        val token = TokenManager.obtenerToken()

        val requestBuilder = request.newBuilder()

        if (!token.isNullOrBlank()) {
            val authorization = if (token.startsWith("Bearer ", ignoreCase = true)) {
                token
            } else {
                "Bearer $token"
            }

            requestBuilder.header("Authorization", authorization)
        }

        return chain.proceed(requestBuilder.build())
    }
}
