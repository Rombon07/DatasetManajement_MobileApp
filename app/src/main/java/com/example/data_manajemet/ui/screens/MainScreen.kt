package com.example.data_manajemet.ui.screens

import MyDataScreen
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data_manajemet.components.BottomBar
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.navigation.BottomNavItem
import com.example.data_manajemet.viewmodel.DatasetViewModel
import com.example.data_manajemet.viewmodel.DatasetViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.data_manajemet.navigation.Screen

@Composable
fun MainScreen(
    navController: NavHostController,
    db: AppDatabase,
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getInt("userId", -1)

    // Debug log userId
    LaunchedEffect(userId) {
        println("Current userId: $userId")
    }

    if (userId == -1) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Login.route) {
                // Clear back stack supaya tidak bisa back ke dashboard tanpa login
                popUpTo(0) { inclusive = true }
            }
        }
        return
    }

    val datasetViewModel: DatasetViewModel = viewModel(
        factory = DatasetViewModelFactory(db.datasetDao())
    )

    val bottomNavItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.MyData,
        BottomNavItem.UploadDataset,
        BottomNavItem.Help,
        BottomNavItem.Settings
    )

    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Dashboard) }

    Scaffold(
        bottomBar = {
            BottomBar(
                items = bottomNavItems,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                is BottomNavItem.Dashboard -> DashboardScreen(datasetViewModel, navController)
                is BottomNavItem.MyData -> MyDataScreen(
                    viewModel = datasetViewModel,
                    navController = navController,
                    userId = userId,
                    onView = { /* TODO: implement view, misal navigasi ke detail */ },
                    onEdit = { dataset ->
                        navController.navigate("editDataset/${dataset.id}")
                    },
                    onDelete = { dataset ->
                        datasetViewModel.deleteDataset(dataset)
                    }
                )
                is BottomNavItem.UploadDataset -> UploadDatasetScreen(
                    viewModel = datasetViewModel,
                    navController = navController,
                    userId = userId
                )
                is BottomNavItem.Help -> HelpScreen()
                is BottomNavItem.Settings -> SettingsScreen(navController)
            }
        }
    }
}
