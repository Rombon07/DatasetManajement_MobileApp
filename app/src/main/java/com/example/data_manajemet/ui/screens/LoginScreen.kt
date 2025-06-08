package com.example.data_manajemet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data_manajemet.data.AppDatabase
import kotlinx.coroutines.launch
import com.example.data_manajemet.navigation.Screen
import androidx.compose.ui.platform.LocalContext
import android.content.Context


@Composable
fun LoginScreen(navController: NavController, db: AppDatabase) {
    val userDao = db.userDao()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            if (username.isBlank() || password.isBlank()) {
                message = "Username dan Password tidak boleh kosong"
                return@Button
            }

            scope.launch {
                val user = userDao.login(username, password)
                if (user != null) {
                    // Simpan userId ke SharedPreferences
                    sharedPreferences.edit().putInt("userId", user.id).apply()

                    message = "Login berhasil"
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                } else {
                    message = "Username atau Password salah"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate("register") {
                popUpTo("login") { inclusive = true }
            }
        }) {
            Text("Belum punya akun? Register")
        }

        Spacer(Modifier.height(8.dp))

        Text(message, color = MaterialTheme.colorScheme.error)
    }
}
