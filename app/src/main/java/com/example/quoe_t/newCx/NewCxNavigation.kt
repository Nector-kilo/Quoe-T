package com.example.quoe_t.newCx

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quoe_t.MainNavigation
import com.example.quoe_t.Routes
import com.example.quoe_t.newCx.screens.NewCxDataScreen
import com.example.quoe_t.newCx.screens.NewCxQuoteScreen

@Composable
fun NewCxNavigation() {
    val navController = rememberNavController()
    val newCxViewModel: NewCxViewModel = viewModel()

    NavHost(navController, startDestination = Routes.NewCxDataScreen) {
        composable<Routes.NewCxDataScreen> {
            NewCxDataScreen(newCxViewModel) { navController.navigate(Routes.NewCxQuoteScreen) }
        }

        composable<Routes.NewCxQuoteScreen> {
            NewCxQuoteScreen(newCxViewModel) { navController.navigate(Routes.HomeScreen) }
        }

        composable<Routes.HomeScreen> { MainNavigation() }
    }
}