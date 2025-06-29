package com.example.data_manajemet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data_manajemet.model.CombinedItem
import com.example.data_manajemet.viewmodel.RequestListViewModel

@Composable
fun RequestListScreen(
    viewModel: RequestListViewModel
) {
    val combinedItems by viewModel.combinedList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "📝 Dataset Requests & Local Datasets",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6A1B9A),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            successMessage?.let {
                Text(
                    text = it,
                    color = Color(0xFF388E3C),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (combinedItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    items(combinedItems.filterIsInstance<CombinedItem.Request>()) { requestItem ->
                        val datasets = combinedItems.filterIsInstance<CombinedItem.Dataset>()
                        RequestCard(
                            item = requestItem,
                            datasetOptions = datasets,
                            onConfirm = { datasetId ->
                                viewModel.konfirmasiPilihDataset(
                                    requestId = requestItem.item.id,
                                    datasetId = datasetId
                                )
                            }
                        )
                    }

                    items(combinedItems.filterIsInstance<CombinedItem.Dataset>()) { datasetItem ->
                        DatasetCard(
                            item = datasetItem,
                            onSendToFriend = {
                                // Panggil ViewModel function sesuai kebutuhan
//                                viewModel.uploadDatasetToFriend(datasetItem.item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCard(
    item: CombinedItem.Request,
    datasetOptions: List<CombinedItem.Dataset>,
    onConfirm: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedDataset by remember { mutableStateOf<CombinedItem.Dataset?>(null) }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFF7E57C2),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.item.nama_model,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "Kebutuhan: ${item.item.kebutuhan}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Deskripsi: ${item.item.deskripsi}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Waktu: ${item.item.timestamp}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDataset?.item?.name ?: "Pilih dataset...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dataset") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    datasetOptions.forEach { dataset ->
                        DropdownMenuItem(
                            text = { Text(dataset.item.name) },
                            onClick = {
                                selectedDataset = dataset
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (selectedDataset != null) {
                Button(
                    onClick = { onConfirm(selectedDataset!!.item.id) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Konfirmasi Pilihan")
                }
            }
        }
    }
}

@Composable
fun DatasetCard(
    item: CombinedItem.Dataset,
    onSendToFriend: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.item.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "Status: ${item.item.status}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Uploaded: ${item.item.uploaded_at}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol kirim dataset
            Button(
                onClick = onSendToFriend,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Kirim ke Teman", color = Color.White)
            }
        }
    }
}
