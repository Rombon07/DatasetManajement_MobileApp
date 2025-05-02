package com.example.data_manajemet

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.database.getStringOrNull
import androidx.core.graphics.drawable.toBitmap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AddNewDatasetForm()
            }
        }
    }
}

data class Dataset(
    val nama: String,
    val deskripsi: String,
    val imageUri: Uri?,
    val fileUri: Uri?
)

@Composable
fun AddNewDatasetForm() {
    var namaDataset by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val datasetList = remember { mutableStateListOf<Dataset>() }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Add New Dataset", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = namaDataset,
            onValueChange = { namaDataset = it },
            label = { Text("Nama Dataset") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = deskripsi,
            onValueChange = { deskripsi = it },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { imageLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Image")
        }

        selectedImageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            val inputStream = context.contentResolver.openInputStream(uri)
            val drawable = Drawable.createFromStream(inputStream, uri.toString())
            drawable?.let {
                val bitmap = it.toBitmap().asImageBitmap()
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { fileLauncher.launch("*/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload File")
        }

        selectedFileUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val name = if (cursor != null && cursor.moveToFirst()) {
                cursor.getStringOrNull(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            } else null
            cursor?.close()
            Text("📁 File terpilih: ${name ?: uri.path}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                datasetList.add(
                    Dataset(namaDataset, deskripsi, selectedImageUri, selectedFileUri)
                )
                namaDataset = ""
                deskripsi = ""
                selectedImageUri = null
                selectedFileUri = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Dataset")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Daftar Dataset", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(datasetList) { dataset ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📌 Nama: ${dataset.nama}", fontWeight = FontWeight.Bold)
                        Text("📝 Deskripsi: ${dataset.deskripsi}")

                        dataset.imageUri?.let { uri ->
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val drawable = Drawable.createFromStream(inputStream, uri.toString())
                            drawable?.let {
                                val bitmap = it.toBitmap().asImageBitmap()
                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }

                        dataset.fileUri?.let { uri ->
                            val cursor = context.contentResolver.query(uri, null, null, null, null)
                            val name = if (cursor != null && cursor.moveToFirst()) {
                                cursor.getStringOrNull(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            } else null
                            cursor?.close()
                            Text("📁 File: ${name ?: uri.path}")
                        }
                    }
                }
            }
        }
    }
}
