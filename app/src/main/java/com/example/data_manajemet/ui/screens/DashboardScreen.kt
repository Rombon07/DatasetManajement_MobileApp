package com.example.data_manajemet.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.viewmodel.DatasetViewModel

@Composable
fun DashboardScreen(
    viewModel: DatasetViewModel,
    navController: NavHostController
) {
    val datasetList by viewModel.datasetList.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filteredDatasets = remember(datasetList, searchQuery) {
        if (searchQuery.text.isBlank()) datasetList
        else datasetList.filter { it.name.contains(searchQuery.text, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard Page",
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Cari dataset...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (filteredDatasets.isEmpty()) {
            Text("Tidak ada dataset yang cocok.", fontSize = 16.sp)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredDatasets) { dataset ->
                    DatasetCard(dataset = dataset) {
                        navController.navigate("datasetDetail/${dataset.id}")
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
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(dataset.coverUri),
                contentDescription = "Cover Image",
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 12.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dataset.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "File: ${Uri.parse(dataset.datasetFileUri).lastPathSegment}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Tanggal: ${dataset.uploadDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
