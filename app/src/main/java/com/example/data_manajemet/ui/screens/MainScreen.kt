package com.example.data_manajemet.ui.screens

import MyDataScreen
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.data_manajemet.components.BottomBar
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.navigation.BottomNavItem
import com.example.data_manajemet.navigation.Screen
import com.example.data_manajemet.repository.DatasetRepository
import com.example.data_manajemet.repository.RequestRepository
import com.example.data_manajemet.ui.screens.UploadDatasetTwoStep.UploadDatasetStep1Screen
import com.example.data_manajemet.viewmodel.DatasetViewModel
import com.example.data_manajemet.viewmodel.DatasetViewModelFactory
import com.example.data_manajemet.viewmodel.RequestListViewModel
import com.example.data_manajemet.viewmodel.RequestListViewModelFactory

@Composable
fun MainScreen(
    navController: NavHostController,
    db: AppDatabase,
    repository: DatasetRepository
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getInt("userId", -1)

    val datasetViewModel: DatasetViewModel = viewModel(
        factory = DatasetViewModelFactory(
            dao = db.datasetDao(),
            repository = repository
        )
    )

    val requestListViewModel: RequestListViewModel = viewModel(
        factory = RequestListViewModelFactory(
            requestRepository = RequestRepository(),
            datasetRepository = repository
        )
    )

    LaunchedEffect(userId) {
        if (userId == -1) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (userId == -1) return

    val bottomNavItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.MyData,
        BottomNavItem.UploadDataset,
        BottomNavItem.RequestList,
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
                is BottomNavItem.Dashboard -> {
                    DashboardScreen(
                        viewModel = datasetViewModel,
                        navController = navController
                    )
                }

                is BottomNavItem.MyData -> {
                    MyDataScreen(
                        viewModel = datasetViewModel,
                        navController = navController,
                        userId = userId,
                        onView = { dataset ->
                            navController.navigate("datasetDetail/${dataset.id}")
                        },
                        onEdit = { dataset ->
                            navController.navigate("editDataset/${dataset.id}")
                        },
                        onDelete = { dataset ->
                            datasetViewModel.deleteDataset(dataset)
                        }
                    )
                }

                is BottomNavItem.UploadDataset -> {
                    UploadDatasetStep1Screen(
                        navController = navController,
                        userId = userId,
                        onNext = { name, description, uploadDate, selectedStatus ->
                            val encodedName = Uri.encode(name)
                            val encodedDescription = Uri.encode(description)
                            val encodedDate = Uri.encode(uploadDate)
                            val encodedStatus = Uri.encode(selectedStatus)

                            navController.navigate(
                                "upload_step2/$userId/$encodedDate/$encodedName/$encodedDescription/$encodedStatus"
                            )
                        }
                    )
                }

                is BottomNavItem.RequestList -> {
                    RequestListScreen(
                        viewModel = requestListViewModel
                    )
                }

                is BottomNavItem.Settings -> {
                    SettingsScreen(navController)
                }

                is BottomNavItem.Help -> {
                    HelpScreen()
                }
            }
        }
    }
}
