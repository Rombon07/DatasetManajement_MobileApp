import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.viewmodel.DatasetViewModel

@Composable
fun MyDataScreen(
    viewModel: DatasetViewModel,
    navController: NavHostController,
    userId: Int,
    onView: (Dataset) -> Unit,
    onEdit: (Dataset) -> Unit,
    onDelete: (Dataset) -> Unit
) {
    val datasets by viewModel.userDatasetList.collectAsState()
    var filterText by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.loadDatasetsByUser(userId)
    }

    val filteredDatasets = remember(datasets, filterText) {
        if (filterText.isBlank()) datasets
        else datasets.filter { it.name.contains(filterText, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📁 My Data Repository",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = filterText,
            onValueChange = { filterText = it },
            label = { Text("Filter by dataset name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        if (filteredDatasets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada data yang sesuai filter.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredDatasets) { dataset ->
                    ExpandableDatasetRow(
                        dataset = dataset,
                        onView = { dataset ->
                            navController.navigate("datasetDetail/${dataset.id}")
                        },
                        onEdit = { navController.navigate("editDataset/${dataset.id}") },
                        onDeleteConfirmed = { viewModel.deleteDataset(it) }
                    )
                    Divider()
                }

            }
        }
    }
}

@Composable
fun ExpandableDatasetRow(
    dataset: Dataset,
    onView: (Dataset) -> Unit,
    onEdit: (Dataset) -> Unit,
    onDeleteConfirmed: (Dataset) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(animationSpec = TweenSpec(durationMillis = 300))
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dataset.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = dataset.uploadDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Deskripsi: ${dataset.description}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "File: ${dataset.datasetFileUri?.substringAfterLast('/') ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (showDeleteConfirm) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                onDeleteConfirmed(dataset)
                                showDeleteConfirm = false
                                expanded = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ya, Hapus")
                        }
                        Button(
                            onClick = { showDeleteConfirm = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal")
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onView(dataset) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)), // Biru
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View")
                        }
                        Button(
                            onClick = { onEdit(dataset) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)), // Oranye
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Edit")
                        }
                        Button(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
