package com.example.data_manajemet.navigation

import MyDataScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.data.DatasetDao
import com.example.data_manajemet.network.DatasetApiService
import com.example.data_manajemet.repository.DatasetRepository
import com.example.data_manajemet.repository.RequestRepository
import com.example.data_manajemet.ui.screens.*
import com.example.data_manajemet.ui.screens.UploadDatasetTwoStep.UploadDatasetStep1Screen
import com.example.data_manajemet.ui.screens.UploadDatasetTwoStep.UploadDatasetStep2Screen
import com.example.data_manajemet.viewmodel.DatasetViewModel
import com.example.data_manajemet.viewmodel.DatasetViewModelFactory
import com.example.data_manajemet.viewmodel.RequestListViewModel
import com.example.data_manajemet.viewmodel.RequestListViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavGraph(
    navController: NavHostController,
    dao: DatasetDao,
    db: AppDatabase
) {
    // Retrofit API Service
    val apiService = remember {
        Retrofit.Builder()
            .baseUrl("http://172.16.59.234:8000/") // Ganti sesuai server kamu
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatasetApiService::class.java)
    }

    val repository = remember { DatasetRepository(apiService, dao) }

    val datasetViewModel: DatasetViewModel = viewModel(
        factory = DatasetViewModelFactory(
            dao = db.datasetDao(),
            repository = repository
        )
    )

    val requestRepository = remember { RequestRepository() }
    val requestListViewModel: RequestListViewModel = viewModel(
        factory = RequestListViewModelFactory(requestRepository, repository)
    )

    LaunchedEffect(Unit) {
        datasetViewModel.syncDatasets()
    }

    NavHost(navController = navController, startDestination = "landing") {

        composable("landing") {
            LandingScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController, db = db)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController, db = db)
        }

        composable(Screen.Dashboard.route) {
            MainScreen(
                navController = navController,
                db = db,
                repository = repository
            )
        }

        composable(
            route = "upload_step1/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            UploadDatasetStep1Screen(
                navController = navController,
                userId = userId,
                onNext = { name, description, uploadDate, selectedStatus ->
                    val encodedName = URLEncoder.encode(name, "UTF-8")
                    val encodedDescription = URLEncoder.encode(description, "UTF-8")
                    val encodedUploadDate = URLEncoder.encode(uploadDate, "UTF-8")
                    val encodedStatus = URLEncoder.encode(selectedStatus, "UTF-8")
                    navController.navigate(
                        "upload_step2/$userId/$encodedUploadDate/$encodedName/$encodedDescription/$encodedStatus"
                    )
                }
            )
        }

        composable(
            route = "upload_step2/{userId}/{uploadDate}/{name}/{description}/{status}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("uploadDate") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val uploadDate = URLDecoder.decode(backStackEntry.arguments?.getString("uploadDate") ?: "", "UTF-8")
            val name = URLDecoder.decode(backStackEntry.arguments?.getString("name") ?: "", "UTF-8")
            val description = URLDecoder.decode(backStackEntry.arguments?.getString("description") ?: "", "UTF-8")
            val status = URLDecoder.decode(backStackEntry.arguments?.getString("status") ?: "", "UTF-8")

            UploadDatasetStep2Screen(
                viewModel = datasetViewModel,
                navController = navController,
                userId = userId,
                uploadDate = uploadDate,
                name = name,
                description = description,
                status = status
            )
        }

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
                    repository = repository,
                    navController = navController
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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

        composable(Screen.RequestList.route) {
            RequestListScreen(
                viewModel = requestListViewModel
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
