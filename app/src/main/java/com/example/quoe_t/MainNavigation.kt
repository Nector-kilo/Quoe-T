package com.example.quoe_t

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quoe_t.newCx.NewCxNavigation

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Routes.HomeScreen) {
        composable<Routes.HomeScreen> { HomeScreen(
            onNewCxClicked = { navController.navigate(Routes.NewCxNavigation) },
            onExistingCxClicked = {} //TODO Navigate to UpgradeScreen.
        )}

        composable<Routes.NewCxNavigation> { NewCxNavigation() }
    }
}