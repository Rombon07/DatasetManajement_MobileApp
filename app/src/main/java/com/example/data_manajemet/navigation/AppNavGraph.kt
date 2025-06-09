package com.example.data_manajemet.navigation

import MyDataScreen
import android.text.Layout
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.viewmodel.DatasetViewModel
import com.example.data_manajemet.viewmodel.DatasetViewModelFactory
import com.example.data_manajemet.ui.screens.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.data.DatasetDao
import com.example.data_manajemet.ui.screens.UploadDatasetTwoStep.UploadDatasetStep1Screen
import com.example.data_manajemet.ui.screens.UploadDatasetTwoStep.UploadDatasetStep2Screen

@Composable
fun AppNavGraph(navController: NavHostController, dao: DatasetDao, db: AppDatabase) {
    // Buat satu instance DatasetViewModel pakai factory sekali di sini
    val datasetViewModel: DatasetViewModel = viewModel(
        factory = DatasetViewModelFactory(dao)
    )

    NavHost(navController = navController, startDestination = "landing") {

        composable("landing") { LandingScreen(navController) }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController, db = db)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController, db = db)
        }

        composable(Screen.Dashboard.route) {
            MainScreen(navController = navController, db = db)
        }

        // UploadDatasetStep1Screen (step pertama)
        composable(
            route = "upload_step1/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            UploadDatasetStep1Screen(
                navController = navController,
                userId = userId,
                onNext = { name, description, uploadDate ->
                    val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                    val encodedDescription = java.net.URLEncoder.encode(description, "UTF-8")
                    val encodedUploadDate = java.net.URLEncoder.encode(uploadDate, "UTF-8")
                    navController.navigate("upload_step2/$userId/$encodedUploadDate/$encodedName/$encodedDescription")
                }
            )
        }

        // UploadDatasetStep2Screen (step kedua), pakai datasetViewModel yang sudah dibuat
        composable(
            route = "upload_step2/{userId}/{uploadDate}/{name}/{description}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("uploadDate") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val uploadDate = backStackEntry.arguments?.getString("uploadDate") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""

            UploadDatasetStep2Screen(
                viewModel = datasetViewModel,
                navController = navController,
                userId = userId,
                uploadDate = uploadDate,
                name = name,
                description = description
            )
        }

        // MyDataScreen dengan userId sebagai argumen
        composable(
            route = "mydata/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
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

        composable("datasetDetail/{datasetId}") { backStackEntry ->
            val datasetId = backStackEntry.arguments?.getString("datasetId")?.toIntOrNull()
            if (datasetId != null) {
                DatasetDetailScreen(
                    datasetId = datasetId,
                    dao = dao,
                    navController = navController
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Dataset ID tidak valid")
                }
            }
        }

        composable(
            route = "editDataset/{datasetId}",
            arguments = listOf(navArgument("datasetId") { type = NavType.IntType })
        ) { backStackEntry ->
            val datasetId = backStackEntry.arguments?.getInt("datasetId") ?: 0
            val datasetState = produceState<Dataset?>(initialValue = null, datasetId) {
                value = datasetViewModel.getDatasetById(datasetId)
            }
            val dataset = datasetState.value

            if (dataset != null) {
                EditDatasetScreen(
                    dataset = dataset,
                    viewModel = datasetViewModel,
                    onSaveSuccess = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(Screen.Help.route) {
            HelpScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
