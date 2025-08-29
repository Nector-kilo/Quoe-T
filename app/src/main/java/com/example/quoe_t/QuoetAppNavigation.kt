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

//TODO Refactor this navchart to only navigate through the NewCx screens.
@Composable
fun QuoetAppNavigation() {
    val navController = rememberNavController()
    val newCxViewModel: NewCxViewModel = viewModel()

    NavHost(navController, startDestination = Home) {
        composable<Home> {
            HomeScreen {
                navController.navigate(Devices)
            }
        }

        composable<Devices> {
            DevicesScreen(newCxViewModel) {
                navController.navigate(Quote)
            }
        }

        composable<Quote> {
            QuoteScreen(newCxViewModel) {
                navController.navigate(Home)
            }
        }
    }
}