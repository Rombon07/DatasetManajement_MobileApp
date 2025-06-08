package com.example.data_manajemet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.graphics.Paint // ✅ YANG BENAR
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.data_manajemet.data.DatasetDao
import com.example.data_manajemet.util.readDatasetPreview
import com.example.data_manajemet.viewmodel.DatasetViewModel
import com.example.data_manajemet.viewmodel.DatasetViewModelFactory
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data_manajemet.data.createBarDataFromPreview
import com.github.mikephil.charting.charts.BarChart

import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data_manajemet.data.createCandleDataFromEntries
import com.example.data_manajemet.data.createCandleEntriesFromPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatasetDetailScreen(
    datasetId: Int,
    dao: DatasetDao,
    navController: NavHostController
) {
    val context = LocalContext.current
    var previewLines by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var selectedColumnIndex by remember { mutableStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }
    val factory = DatasetViewModelFactory(dao)
    val viewModel: DatasetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
    val dataset by viewModel.getDatasetByIdFlow(datasetId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Dataset") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (dataset == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LaunchedEffect(dataset!!.datasetFileUri) {
                val filePath = dataset!!.datasetFileUri
                if (!filePath.isNullOrEmpty()) {
                    previewLines = readDatasetPreview(filePath)
                }
            }

            val header = previewLines.firstOrNull() ?: emptyList()
            val rows = previewLines.drop(1)

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Detail info
                Text("Nama Dataset: ${dataset!!.name}", style = MaterialTheme.typography.titleLarge)
                Text(dataset!!.description ?: "-", style = MaterialTheme.typography.bodyLarge)
                Text("Tanggal Upload: ${dataset!!.uploadDate}", style = MaterialTheme.typography.bodyMedium)

                Divider()

                // Preview Table
                Text("Preview File (5 baris pertama):", style = MaterialTheme.typography.titleMedium)

                if (previewLines.isEmpty()) {
                    Text("Gagal membaca isi file atau file kosong.")
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6FF))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            // Menu kanan atas
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box {
                                    IconButton(onClick = { menuExpanded = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                    }

                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Download") },
                                            onClick = { menuExpanded = false /* TODO */ }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Cetak") },
                                            onClick = { menuExpanded = false /* TODO */ }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Use API") },
                                            onClick = { menuExpanded = false /* TODO */ }
                                        )
                                    }
                                }
                            }

                            val scrollState = rememberScrollState()
                            Column(modifier = Modifier.horizontalScroll(scrollState)) {
                                // Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFDCCEFF))
                                        .padding(vertical = 8.dp)
                                ) {
                                    header.forEach {
                                        Text(
                                            text = it,
                                            modifier = Modifier
                                                .width(120.dp)
                                                .padding(horizontal = 6.dp),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color(0xFF3D0066)
                                        )
                                    }
                                }

                                Divider(color = Color.LightGray)

                                // Rows
                                rows.take(5).forEach { row ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        row.forEach {
                                            Text(
                                                text = it,
                                                modifier = Modifier
                                                    .width(120.dp)
                                                    .padding(horizontal = 6.dp),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Pilihan kolom untuk visualisasi
                    Text("Pilih Kolom untuk Visualisasi:", style = MaterialTheme.typography.titleMedium)
                    if (header.isNotEmpty()) {
                        DropdownMenuBox(
                            options = header,
                            selectedIndex = selectedColumnIndex,
                            onSelectedChange = { selectedColumnIndex = it }
                        )
                    } else {
                        Text("Tidak ada kolom untuk dipilih.")
                    }

                    // Bar Chart
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Bar Chart", style = MaterialTheme.typography.titleMedium)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6FF))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                BarChart(ctx).apply {
                                    data = createBarDataFromPreview(previewLines, selectedColumnIndex)
                                    description.isEnabled = false
                                    legend.isEnabled = true
                                    animateY(1000)
                                    invalidate()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Boxplot (Candlestick chart)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Boxplot (Candlestick Chart) untuk Outlier", style = MaterialTheme.typography.titleMedium)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6FF))
                    ) {
                        AndroidView(factory = { ctx ->
                            CandleStickChart(ctx).apply {
                                description.isEnabled = false
                                legend.isEnabled = false
                                setDrawGridBackground(false)
                                axisRight.isEnabled = false

                                val candleEntries = createCandleEntriesFromPreview(previewLines, selectedColumnIndex)
                                val candleData = createCandleDataFromEntries(candleEntries)

                                val dataSet = CandleDataSet(candleEntries, "Boxplot").apply {
                                    color = Color(0xFF3D0066).toArgb()
                                    shadowColor = Color.DarkGray.toArgb()
                                    shadowWidth = 1f
                                    decreasingColor = Color(0xFF6200EE).toArgb()
                                    decreasingPaintStyle = Paint.Style.FILL
                                    increasingColor = Color(0xFF6200EE).toArgb()
                                    increasingPaintStyle = Paint.Style.FILL
                                    neutralColor = Color.Gray.toArgb()
                                    setDrawValues(false)
                                }
                                data = CandleData(dataSet)

                                animateY(1000)
                                invalidate()
                            }
                        }, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}


// Helper composable dropdown menu box untuk pilih kolom
@Composable
fun DropdownMenuBox(
    options: List<String>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(options.getOrNull(selectedIndex) ?: "Pilih kolom")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
