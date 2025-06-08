package com.example.data_manajemet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.data.User
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, db: AppDatabase) {
    val userDao = db.userDao()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

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

        Spacer(Modifier.height(8.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
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
            if (password != confirmPassword) {
                message = "Password dan konfirmasi tidak cocok"
                return@Button
            }

            scope.launch {
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    message = "Username sudah dipakai"
                } else {
                    userDao.insert(User(username = username, password = password))
                    message = "Registrasi berhasil, silakan login"
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }

        TextButton(onClick = {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }) {
            Text("Sudah punya akun? Ayo Login disini!")
        }

        Spacer(Modifier.height(8.dp))

        Text(message, color = MaterialTheme.colorScheme.error)
    }
}
