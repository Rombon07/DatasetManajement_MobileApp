package com.example.data_manajemet.ui.screens.UploadDatasetTwoStep

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data_manajemet.ui.components.DatePickerDialog
import com.example.data_manajemet.viewmodel.DatasetViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDatasetStep1Screen(
    navController: NavController,
    userId: Int,
    onNext: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("New") }
    var expanded by remember { mutableStateOf(false) }
    var uploadDate by remember { mutableStateOf(LocalDate.now()) }
    val statusOptions = listOf("New", "On Progress", "Complete")

    val isFormValid = name.isNotBlank() && description.isNotBlank() && selectedStatus.isNotBlank()

    val scrollState = rememberScrollState()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uploadDate.toEpochDay() * 24 * 60 * 60 * 1000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F3F8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Dataset",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name of Dataset") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Fill the Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )

                Text("Upload Date", fontWeight = FontWeight.Medium)

                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Status Dataset", fontWeight = FontWeight.Medium)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Choose Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    selectedStatus = status
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                            Date(millis)
                        )
                        onNext(name, description, dateStr, selectedStatus)
                    },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Selanjutnya", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
