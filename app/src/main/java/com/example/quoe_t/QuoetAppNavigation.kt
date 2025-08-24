package com.example.quoe_t

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quoe_t.screens.DevicesScreen
import com.example.quoe_t.screens.HomeScreen
import com.example.quoe_t.screens.QuoteScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Devices

@Serializable
object Quote

@Composable
fun QuoetAppNavigation() {
    val navController = rememberNavController()
    val quoetViewModel: QuoetViewModel = viewModel()

    NavHost(navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(quoetViewModel) {
                navController.navigate(Devices)
            }
        }

        composable<Devices> {
            DevicesScreen(quoetViewModel) {
                navController.navigate(Quote)
            }
        }

        composable<Quote> {
            QuoteScreen(quoetViewModel) {
                quoetViewModel.clearViewModel()
                navController.navigate(Home)
            }
        }
    }
}