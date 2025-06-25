package com.example.data_manajemet.ui.screens.UploadDatasetTwoStep

import android.net.Uri
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.util.copyFileToInternalStorage
import com.example.data_manajemet.viewmodel.DatasetViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UploadDatasetStep2Screen(
    viewModel: DatasetViewModel,
    navController: NavController,
    userId: Int,
    uploadDate: String,
    name: String,
    description: String,
    status: String
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
    ) { granted -> hasCameraPermission = granted }

    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri -> coverUri = uri; savedCoverPath = null }
    }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri -> datasetFileUri = uri }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            coverUri = cameraImageUri
            savedCoverPath = null
        }
    }

    fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timestamp"
        val storageDir = context.cacheDir
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F3F8)) // Warna background lebih soft
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Upload Dataset",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4A148C), // Warna ungu lebih elegan
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {},
                label = { Text("Dataset Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            OutlinedTextField(
                value = description,
                onValueChange = {},
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            Text("Cover Image", fontWeight = FontWeight.Medium, fontSize = 14.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F1F1))
                    .clickable { coverLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Text("Upload or Take Picture\n(.png, .jpg, .jpeg)", color = Color.Gray, fontSize = 13.sp)
            }

            coverUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Text("Dataset File", fontWeight = FontWeight.Medium, fontSize = 14.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F1F1))
                    .clickable { fileLauncher.launch("*/*") },
                contentAlignment = Alignment.Center
            ) {
                Text("Choose .csv or .xlsx file", color = Color.Gray, fontSize = 13.sp)
            }

            datasetFileUri?.let {
                Text("File: ${it.lastPathSegment}", style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    if (coverUri == null || datasetFileUri == null) {
                        message = "Cover dan file dataset harus diunggah."
                        return@Button
                    }

                    val coverPath = copyFileToInternalStorage(context, coverUri!!, "cover_${System.currentTimeMillis()}.jpg")
                    val datasetPath = copyFileToInternalStorage(context, datasetFileUri!!, "dataset_${System.currentTimeMillis()}.csv")

                    if (coverPath == null || datasetPath == null) {
                        message = "Gagal menyimpan file. Coba lagi."
                        return@Button
                    }

                    savedCoverPath = coverPath

                    viewModel.addDataset(
                        Dataset(
                            name = name,
                            description = description,
                            coverUri = coverPath,
                            datasetFileUri = datasetPath,
                            uploadDate = uploadDate,
                            status = status,
                            userId = userId
                        )
                    )

                    navController.navigate("dashboard") {
                        popUpTo("upload_step1/$userId") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
            ) {
                Text("Upload Dataset", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (message.isNotEmpty()) {
                Text(message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
        }
    }
}
