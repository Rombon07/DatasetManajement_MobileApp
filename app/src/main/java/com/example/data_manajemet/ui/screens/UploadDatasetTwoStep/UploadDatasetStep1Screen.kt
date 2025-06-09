package com.example.data_manajemet.ui.screens.UploadDatasetTwoStep

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data_manajemet.ui.components.DatePickerDialog
import com.example.data_manajemet.viewmodel.DatasetViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDatasetStep1Screen(
    navController: NavController,
    userId: Int,
    onNext: (String, String, String) -> Unit // Kirim data ke step selanjutnya
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var uploadDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateChange = { dateInMillis ->
                val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val date = formatter.format(java.util.Date(dateInMillis))
                uploadDate = date
                showDatePicker = false
            }
        )
    }

    // Cek apakah semua kolom sudah terisi
    val isFormValid = name.isNotBlank() && description.isNotBlank() && uploadDate.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Upload Dataset", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Dataset") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi Dataset") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
            Text(if (uploadDate.isBlank()) "Pilih Tanggal Upload" else "Tanggal: $uploadDate")
        }

        Button(
            onClick = {
                // Kirim data ke langkah selanjutnya
                onNext(name, description, uploadDate)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid  // Hanya aktif jika form valid
        ) {
            Text("Berikutnya")
        }
    }
}
