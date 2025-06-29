package com.example.data_manajemet.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.data.User
import com.example.data_manajemet.network.RetrofitInstance
import com.example.data_manajemet.viewmodel.DatasetViewModel

@Composable
fun DashboardScreen(
    viewModel: DatasetViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val userDao = remember { AppDatabase.getInstance(context).userDao() }

    // Data Room
    val datasetRoom by viewModel.datasetList.collectAsState()

    // Data API state (seperti di RequestListScreen)
    var datasetApi by remember { mutableStateOf<List<Dataset>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Search
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    // Fetch user
    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("userId", -1)
        if (userId != -1) {
            val user = userDao.getUserById(userId)
            currentUser = user
        }
    }

    // Fetch API
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val apiResponse = RetrofitInstance.api.getDatasets()
            datasetApi = apiResponse.map {
                Dataset(
                    id = it.id ?: 0,
                    name = it.name ?: "-",
                    description = it.description ?: "-",
                    coverUri = it.cover_image ?: "",
                    datasetFileUri = it.data_file ?: "",
                    uploadDate = it.uploaded_at ?: "-",
                    status = it.status ?: "-",
                    userId = it.owner ?: 0
                )
            }
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
        } finally {
            isLoading = false
        }
    }


    // Gabungkan data Room + API
    val allDatasets = remember(datasetRoom, datasetApi) {
        datasetRoom + datasetApi
    }

    // Filter search
    val filteredDatasets = remember(allDatasets, searchQuery) {
        if (searchQuery.text.isBlank()) allDatasets
        else allDatasets.filter {
            it.name.contains(searchQuery.text, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background grid aesthetic
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 40f
            val color = Color(0xFFEDE7F6)
            val width = size.width
            val height = size.height
            for (x in 0..(width / gridSize).toInt()) {
                for (y in 0..(height / gridSize).toInt()) {
                    drawRect(
                        color = color.copy(alpha = 0.2f),
                        topLeft = androidx.compose.ui.geometry.Offset(x * gridSize, y * gridSize),
                        size = androidx.compose.ui.geometry.Size(gridSize, gridSize)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Sapaan pengguna
            currentUser?.let {
                Text(
                    text = "Hai, ${it.username} 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF6A1B9A)
                )
                Text(
                    text = "Selamat datang kembali! Kelola dataset kamu di sini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } ?: Text("Memuat data pengguna...", color = Color.Gray)

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari dataset...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7E57C2),
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color(0xFF7E57C2)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading/error indicator
            if (isLoading) {
                Text("Memuat data dari server...", color = Color.Gray)
            } else if (errorMessage != null) {
                Text(
                    "Gagal memuat data: $errorMessage",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Daftar dataset
            if (filteredDatasets.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada dataset yang cocok.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredDatasets) { dataset ->
                        DatasetCard(
                            dataset = dataset,
                            onClick = { navController.navigate("datasetDetail/${dataset.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatasetCard(
    dataset: Dataset,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(dataset.coverUri),
                contentDescription = "Cover",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dataset.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF4A148C))
                )
                Text(
                    text = "File: ${Uri.parse(dataset.datasetFileUri).lastPathSegment}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Text(
                    text = "Tanggal: ${dataset.uploadDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}
