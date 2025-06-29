package com.example.data_manajemet.navigation

import MyDataScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.network.DatasetApiService
import com.example.data_manajemet.repository.DatasetRepository
import com.example.data_manajemet.ui.screens.*
import com.example.data_manajemet.viewmodel.DatasetViewModel
import com.example.data_manajemet.viewmodel.DatasetViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun NavigationGraphWithBottomBar(
    navController: NavHostController,
    db: AppDatabase
) {
    // Buat Retrofit ApiService
    val apiService = remember {
        Retrofit.Builder()
            .baseUrl("http://172.16.59.234:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.example.data_manajemet.network.DatasetApiService::class.java)
    }

    // Buat Repository
        val repository = remember {
            DatasetRepository(apiService, db.datasetDao())
        }

    // Buat ViewModel
        val datasetViewModel: DatasetViewModel = viewModel(
            factory = DatasetViewModelFactory(db.datasetDao(), repository)
        )


    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = datasetViewModel,
                navController = navController
            )
        }
        composable(
            route = Screen.MyData.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            MyDataScreen(
                viewModel = datasetViewModel,
                navController = navController,
                userId = userId,
                onView = { dataset ->
                    // Implementasi navigasi ke halaman detail dataset
                    navController.navigate("detail/${dataset.id}")
                },
                onEdit = { dataset ->
                    // Implementasi navigasi ke halaman edit dataset
                    navController.navigate("edit/${dataset.id}")
                },
                onDelete = { dataset ->
                    // Panggil fungsi hapus dari viewModel
                    datasetViewModel.deleteDataset(dataset)
                }
            )
        }
        composable(Screen.Help.route) {
            HelpScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
