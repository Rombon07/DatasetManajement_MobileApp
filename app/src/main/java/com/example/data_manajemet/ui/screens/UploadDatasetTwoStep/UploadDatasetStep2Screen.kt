package com.example.data_manajemet.ui.screens.UploadDatasetTwoStep

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
import com.example.data_manajemet.util.copyFileToInternalStorage
import com.example.data_manajemet.viewmodel.DatasetViewModel
import java.io.File

@Composable
fun UploadDatasetStep2Screen(
    viewModel: DatasetViewModel,
    navController: NavController,
    userId: Int,
    uploadDate: String,
    name: String,
    description: String
) {
    val context = LocalContext.current

    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var datasetFileUri by remember { mutableStateOf<Uri?>(null) }
    var savedCoverPath by remember { mutableStateOf<String?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var message by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coverUri = uri
            savedCoverPath = null
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            datasetFileUri = uri
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            coverUri = cameraImageUri
            savedCoverPath = null
        }
    }

    fun createImageFile(): File {
        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val fileName = "IMG_$timestamp"
        val storageDir = context.cacheDir
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Upload Dataset - Step 2", style = MaterialTheme.typography.headlineMedium)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { coverLauncher.launch("image/*") }, modifier = Modifier.weight(1f)) {
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
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
        }

        Button(onClick = { fileLauncher.launch("*/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Unggah File Dataset (.csv / .xlsx)")
        }

        datasetFileUri?.let {
            Text("File terpilih: ${it.lastPathSegment ?: "Unknown"}")
        }

        Button(
            onClick = {
                if (coverUri == null || datasetFileUri == null) {
                    message = "Cover dan file dataset harus diunggah"
                    return@Button
                }

                val coverPath = copyFileToInternalStorage(context, coverUri!!, "cover_${System.currentTimeMillis()}.jpg")
                val datasetFilePath = copyFileToInternalStorage(context, datasetFileUri!!, "dataset_${System.currentTimeMillis()}.csv")

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
                navController.navigate("dashboard") { popUpTo("upload/$userId") { inclusive = true } }
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
