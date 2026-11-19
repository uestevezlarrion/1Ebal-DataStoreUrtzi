package com.example.applicationdatastore.ui.pantailak

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomePantaila(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ongi etorri!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Erabiltzailea: ${user?.email ?: "Ezezaguna"}",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("lpantaila") {
                    popUpTo("home") { inclusive = true }
                }
            }
        ) {
            Text("Saioa itxi (Logout)")
        }
    }
}
