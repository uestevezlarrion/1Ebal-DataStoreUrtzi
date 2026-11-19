package com.example.applicationdatastore.ui.pantailak

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "saioaren_datuak")

private val EMAIL_GAKOA = stringPreferencesKey("erabiltzaile_emaila")
private val PASAHITZA_GAKOA = stringPreferencesKey("erabiltzaile_pasahitza")
private val GOGORATU_GAKOA = booleanPreferencesKey("erabiltzailea_gogoratu")

@Composable
fun LPantaila(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    val gordetakoDatuak by context.dataStore.data.collectAsState(
        initial = emptyPreferences()
    )

    var emaila by remember { mutableStateOf("") }
    var pasahitza by remember { mutableStateOf("") }
    var gogoratu by remember { mutableStateOf(false) }
    var mezua by remember { mutableStateOf("") }

    // DataStore-tik berreskuratutako datuak pantailan kargatu.
    LaunchedEffect(gordetakoDatuak) {
        val gogoratuGordea = gordetakoDatuak[GOGORATU_GAKOA] ?: false
        gogoratu = gogoratuGordea
        if (gogoratuGordea) {
            emaila = gordetakoDatuak[EMAIL_GAKOA] ?: ""
            pasahitza = gordetakoDatuak[PASAHITZA_GAKOA] ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(105.dp))

        Text(
            text = "Saioa hasi",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(45.dp))

        OutlinedTextField(
            value = emaila,
            onValueChange = {
                emaila = it
                mezua = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = pasahitza,
            onValueChange = {
                pasahitza = it
                mezua = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (emaila.isBlank() || pasahitza.isBlank()) {
                        mezua = "Bete emaila eta pasahitza."
                    } else {
                        auth.signInWithEmailAndPassword(emaila, pasahitza)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    scope.launch {
                                        context.dataStore.edit { datuak ->
                                            if (gogoratu) {
                                                datuak[EMAIL_GAKOA] = emaila
                                                datuak[PASAHITZA_GAKOA] = pasahitza
                                                datuak[GOGORATU_GAKOA] = true
                                            } else {
                                                datuak.remove(EMAIL_GAKOA)
                                                datuak.remove(PASAHITZA_GAKOA)
                                                datuak[GOGORATU_GAKOA] = false
                                            }
                                        }
                                    }
                                    navController.navigate("home") {
                                        popUpTo("lpantaila") { inclusive = true }
                                    }
                                } else {
                                    mezua = "Errorea saioa hastean: ${task.exception?.localizedMessage}"
                                }
                            }
                    }
                }
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    if (emaila.isBlank() || pasahitza.isBlank()) {
                        mezua = "Bete emaila eta pasahitza erregistratzeko."
                    } else {
                        auth.createUserWithEmailAndPassword(emaila, pasahitza)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    scope.launch {
                                        context.dataStore.edit { datuak ->
                                            if (gogoratu) {
                                                datuak[EMAIL_GAKOA] = emaila
                                                datuak[PASAHITZA_GAKOA] = pasahitza
                                                datuak[GOGORATU_GAKOA] = true
                                            } else {
                                                datuak.remove(EMAIL_GAKOA)
                                                datuak.remove(PASAHITZA_GAKOA)
                                                datuak[GOGORATU_GAKOA] = false
                                            }
                                        }
                                    }
                                    navController.navigate("home") {
                                        popUpTo("lpantaila") { inclusive = true }
                                    }
                                } else {
                                    mezua = "Errorea erregistroan: ${task.exception?.localizedMessage}"
                                }
                            }
                    }
                }
            ) {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = gogoratu,
                onCheckedChange = { gogoratu = it }
            )
            Text("Gogoratu kontua")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mezua.isNotBlank()) {
            Text(
                text = mezua,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
