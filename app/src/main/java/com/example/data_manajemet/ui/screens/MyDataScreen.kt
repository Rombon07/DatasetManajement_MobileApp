import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF3E5F5), Color(0xFFFFFFFF))
                )
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📁 My Data Repository",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6A1B9A),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it },
                label = { Text("Cari nama dataset...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7E57C2),
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color(0xFF7E57C2)
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            if (filteredDatasets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada dataset yang cocok.", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(filteredDatasets) { dataset ->
                        ExpandableDatasetRow(
                            dataset = dataset,
                            onView = onView,
                            onEdit = onEdit,
                            onDeleteConfirmed = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    textColor: Color = Color.White,
    iconTint: Color = Color.White,
    outlined: Boolean = false
) {
    val buttonModifier = Modifier
        .weight(1f)
        .height(42.dp) // agak lebih kecil, padat

    val content: @Composable RowScope.() -> Unit = {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 2.dp) // spasi minimal antara icon dan teks
        )
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall, // lebih kecil
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }

    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            contentPadding = PaddingValues(horizontal = 4.dp), // minimal padding
            colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
            content = content
        )
    } else {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            contentPadding = PaddingValues(horizontal = 4.dp), // minimal padding
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            content = content
        )
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dataset.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF4A148C)
                    )
                    Text(
                        text = dataset.uploadDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Deskripsi: ${dataset.description}",
                    style = MaterialTheme.typography.bodyMedium
                )

                dataset.datasetFileUri?.let {
                    Text(
                        text = "File: ${it.substringAfterLast('/')}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (showDeleteConfirm) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActionButton(
                            onClick = {
                                onDeleteConfirmed(dataset)
                                showDeleteConfirm = false
                            },
                            icon = Icons.Default.Delete,
                            label = "Hapus",
                            backgroundColor = MaterialTheme.colorScheme.error
                        )

                        ActionButton(
                            onClick = { showDeleteConfirm = false },
                            icon = Icons.Default.Close,
                            label = "Batal",
                            backgroundColor = Color.LightGray,
                            textColor = Color.Black,
                            iconTint = Color.Black
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActionButton(
                            onClick = { onView(dataset) },
                            icon = Icons.Default.Visibility,
                            label = "Lihat",
                            backgroundColor = Color(0xFF42A5F5)
                        )

                        ActionButton(
                            onClick = { onEdit(dataset) },
                            icon = Icons.Default.Edit,
                            label = "Edit",
                            backgroundColor = Color(0xFFFFA726)
                        )

                        ActionButton(
                            onClick = { showDeleteConfirm = true },
                            icon = Icons.Default.Delete,
                            label = "Hapus",
                            backgroundColor = Color.Transparent,
                            textColor = MaterialTheme.colorScheme.error,
                            iconTint = MaterialTheme.colorScheme.error,
                            outlined = true
                        )
                    }
                }
            }
        }
    }
}
