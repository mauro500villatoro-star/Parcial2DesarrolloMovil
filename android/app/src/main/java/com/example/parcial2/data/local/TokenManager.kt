package com.example.parcial2.data.local

import android.content.Context

object TokenManager {

    private const val PREFS_NAME = "geotracker_session"
    private const val TOKEN_KEY = "jwt_token"

    private lateinit var preferences: android.content.SharedPreferences

    fun init(context: Context) {
        preferences = context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun guardarToken(token: String) {
        checkInitialized()
        preferences.edit()
            .putString(TOKEN_KEY, token)
            .apply()
    }

    fun obtenerToken(): String? {
        checkInitialized()
        return preferences.getString(TOKEN_KEY, null)
    }

    fun cerrarSesion() {
        checkInitialized()
        preferences.edit()
            .remove(TOKEN_KEY)
            .apply()
    }

    private fun checkInitialized() {
        check(::preferences.isInitialized) {
            "TokenManager debe inicializarse antes de utilizarse"
        }
    }
}
