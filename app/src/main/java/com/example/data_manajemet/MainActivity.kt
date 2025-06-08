package com.example.data_manajemet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.data_manajemet.data.AppDatabase
import com.example.data_manajemet.navigation.AppNavGraph
import com.example.data_manajemet.ui.theme.DataManajemetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.datasetDao()

        setContent {
            DataManajemetTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController, dao = dao, db = db)
                }
            }
        }
    }
}
