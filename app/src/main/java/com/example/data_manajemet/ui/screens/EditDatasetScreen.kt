package com.example.data_manajemet.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.ui.components.DatePickerDialog
import com.example.data_manajemet.viewmodel.DatasetViewModel
import com.example.data_manajemet.util.copyFileToInternalStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditDatasetScreen(
    dataset: Dataset,
    viewModel: DatasetViewModel,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(dataset.name) }
    var description by remember { mutableStateOf(dataset.description ?: "") }
    var coverUri by remember { mutableStateOf(dataset.coverUri?.let { Uri.parse(it) }) }
    var datasetFileUri by remember { mutableStateOf(dataset.datasetFileUri?.let { Uri.parse(it) }) }
    var uploadDate by remember { mutableStateOf(dataset.uploadDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coverUri = uri
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            datasetFileUri = uri
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateChange = { dateInMillis ->
                val date = Date(dateInMillis)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                uploadDate = formatter.format(date)
                showDatePicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Dataset", style = MaterialTheme.typography.headlineMedium)

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

        Button(onClick = { coverLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Ubah Cover Gambar")
        }

        // Preview cover image dari Uri baru atau path lama
        (coverUri?.toString() ?: dataset.coverUri)?.let { path ->
            Image(
                painter = rememberAsyncImagePainter(
                    model = if (path.startsWith("content://")) Uri.parse(path) else File(path)
                ),
                contentDescription = "Cover Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }

        Button(onClick = { fileLauncher.launch("*/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Ubah File Dataset (.csv / .xlsx)")
        }

        datasetFileUri?.let {
            Text("File terpilih: ${it.lastPathSegment}")
        }

        if (message.isNotEmpty()) {
            Text(message, color = MaterialTheme.colorScheme.error)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (name.isBlank() || uploadDate.isBlank()) {
                        message = "Nama dan tanggal wajib diisi"
                        return@Button
                    }

                    // Salin file baru jika ada perubahan
                    val newCoverPath = coverUri?.let {
                        if (it.toString() != dataset.coverUri) {
                            copyFileToInternalStorage(context, it, "cover_${System.currentTimeMillis()}.jpg")
                        } else dataset.coverUri
                    } ?: dataset.coverUri

                    val newDatasetFilePath = datasetFileUri?.let {
                        if (it.toString() != dataset.datasetFileUri) {
                            copyFileToInternalStorage(context, it, "dataset_${System.currentTimeMillis()}.csv")
                        } else dataset.datasetFileUri
                    } ?: dataset.datasetFileUri

                    if (newCoverPath == null || newDatasetFilePath == null) {
                        message = "Gagal menyimpan file baru."
                        return@Button
                    }

                    val updatedDataset = dataset.copy(
                        name = name.trim(),
                        description = description.trim(),
                        coverUri = newCoverPath,
                        datasetFileUri = newDatasetFilePath,
                        uploadDate = uploadDate
                    )

                    viewModel.updateDataset(updatedDataset)
                    onSaveSuccess()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Simpan")
            }

            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Batal")
            }
        }
    }
}
