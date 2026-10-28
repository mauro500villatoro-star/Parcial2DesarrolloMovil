package com.example.parcial2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial2.data.local.TokenManager
import com.example.parcial2.ui.dashboard.DashboardScreen
import com.example.parcial2.ui.login.LoginScreen
import com.example.parcial2.ui.mapa.MapaScreen
import com.example.parcial2.ui.ubicaciones.ListaUbicacionesScreen
import com.example.parcial2.ui.ubicaciones.RegistroUbicacionScreen

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val REGISTRAR = "registrar"
    const val LISTADO = "listado"
    const val MAPA = "mapa"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val tokenExiste = !TokenManager.obtenerToken().isNullOrBlank()

    NavHost(
        navController = navController,
        startDestination = if (tokenExiste) Routes.DASHBOARD else Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onRegistrar = {
                    navController.navigate(Routes.REGISTRAR)
                },
                onListado = {
                    navController.navigate(Routes.LISTADO)
                },
                onMapa = {
                    navController.navigate(Routes.MAPA)
                },
                onCerrarSesion = {
                    TokenManager.cerrarSesion()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTRAR) {
            RegistroUbicacionScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.LISTADO) {
            ListaUbicacionesScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.MAPA) {
            MapaScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
