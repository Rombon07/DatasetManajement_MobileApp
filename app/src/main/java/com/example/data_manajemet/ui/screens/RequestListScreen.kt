package com.example.data_manajemet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data_manajemet.viewmodel.RequestListViewModel
import com.example.data_manajemet.data.remote.DatasetRequestItem

@Composable
fun RequestListScreen(viewModel: RequestListViewModel = viewModel()) {
    val datasetRequests by viewModel.datasetRequests.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Daftar Permintaan Dataset", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Text("Terjadi kesalahan: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(datasetRequests) { item ->
                    RequestItemCard(item)
                }
            }
        }
    }
}

@Composable
fun RequestItemCard(item: DatasetRequestItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Model: ${item.nama_model}", style = MaterialTheme.typography.titleMedium)
            Text("Kebutuhan: ${item.kebutuhan}")
            Text("Deskripsi: ${item.deskripsi}")

            if (item.dataset != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("📦 Dataset Terpilih:")
                Text("- Nama: ${item.dataset.name}")
                Text("- Deskripsi: ${item.dataset.description}")
                Text("- Status: ${item.dataset.status}")
                item.dataset.data_file?.let {
                    Text("- File: ${it}", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text("❌ Belum ada dataset terpilih", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
