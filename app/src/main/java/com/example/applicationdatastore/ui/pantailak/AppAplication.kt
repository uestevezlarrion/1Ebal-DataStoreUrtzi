package com.example.applicationdatastore.ui.pantailak

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppAplication() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDest = if (currentUser != null) "home" else "lpantaila"

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable("lpantaila") {
            LPantaila(navController)
        }
        composable("home") {
            HomePantaila(navController)
        }
    }
}
