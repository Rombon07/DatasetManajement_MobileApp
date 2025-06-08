package com.example.data_manajemet.ui.screens

import android.net.Uri
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.ui.components.DatePickerDialog
import com.example.data_manajemet.util.copyFileToInternalStorage
import com.example.data_manajemet.viewmodel.DatasetViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UploadDatasetScreen(
    viewModel: DatasetViewModel,
    navController: NavController,
    userId: Int
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var datasetFileUri by remember { mutableStateOf<Uri?>(null) }
    var uploadDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var savedCoverPath by remember { mutableStateOf<String?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()

    // Cek permission kamera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Launcher untuk pilih gambar dari galeri
    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coverUri = uri
            savedCoverPath = null
        }
    }

    // Launcher untuk pilih file dataset (.csv/.xlsx)
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            datasetFileUri = uri
        }
    }

    // Launcher untuk ambil gambar dari kamera
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            coverUri = cameraImageUri
            savedCoverPath = null
        }
    }

    // Fungsi buat file gambar sementara di cache app
    fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timestamp"
        val storageDir = context.cacheDir
        return File.createTempFile(fileName, ".jpg", storageDir)
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

        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uploadDate.isBlank()) "Pilih Tanggal Upload" else "Tanggal: $uploadDate")
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { coverLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Pilih Gambar")
            }

            Button(
                onClick = {
                    if (!hasCameraPermission) {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    } else {
                        val imageFile = createImageFile()
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            imageFile
                        )
                        cameraImageUri = uri
                        takePictureLauncher.launch(uri)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ambil Gambar")
            }
        }

        (savedCoverPath ?: coverUri?.toString())?.let { path ->
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

        Button(
            onClick = { fileLauncher.launch("*/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unggah File Dataset (.csv / .xlsx)")
        }

        datasetFileUri?.let {
            Text("File terpilih: ${it.lastPathSegment ?: "Unknown"}")
        }

        Button(
            onClick = {
                if (name.isBlank() || description.isBlank() || uploadDate.isBlank()) {
                    message = "Semua field wajib diisi termasuk tanggal"
                    return@Button
                }
                if (coverUri == null || datasetFileUri == null) {
                    message = "Cover dan file dataset harus diunggah"
                    return@Button
                }

                val coverPath = copyFileToInternalStorage(
                    context,
                    coverUri!!,
                    "cover_${System.currentTimeMillis()}.jpg"
                )

                val datasetFilePath = copyFileToInternalStorage(
                    context,
                    datasetFileUri!!,
                    "dataset_${System.currentTimeMillis()}.csv"
                )

                if (coverPath == null || datasetFilePath == null) {
                    message = "Gagal menyimpan file. Coba lagi."
                    return@Button
                }

                savedCoverPath = coverPath

                val dataset = Dataset(
                    name = name,
                    description = description,
                    coverUri = coverPath,
                    datasetFileUri = datasetFilePath,
                    uploadDate = uploadDate,
                    userId = userId
                )

                viewModel.addDataset(dataset)

                // Navigasi ke MyData dengan userId, popUpTo route upload dengan userId juga
                navController.navigate("dashboard") {
                    popUpTo("upload/$userId") { inclusive = true }
                }

                // Reset form (optional, karena sudah pindah halaman)
                name = ""
                description = ""
                coverUri = null
                datasetFileUri = null
                uploadDate = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Dataset")
        }

        if (message.isNotEmpty()) {
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
