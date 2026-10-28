
package com.example.parcial2

import android.os.Bundle
import com.example.parcial2.ui.navigation.AppNavigation
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.parcial2.data.local.TokenManager
import com.example.parcial2.ui.theme.Parcial2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el almacenamiento del token JWT
        TokenManager.init(applicationContext)

        enableEdgeToEdge()

        setContent {
            Parcial2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
