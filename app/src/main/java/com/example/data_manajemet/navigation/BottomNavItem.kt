package com.example.data_manajemet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Dashboard : BottomNavItem("dashboard", Icons.Filled.Home, "Dashboard")
    object MyData : BottomNavItem("mydata", Icons.Filled.List, "My Data")
    object UploadDataset : BottomNavItem("upload_dataset", Icons.Default.AddCircle, "Upload")
    object Help : BottomNavItem("help", Icons.Filled.Help, "Help")
    object Settings : BottomNavItem("settings", Icons.Filled.Settings, "Settings")
}
