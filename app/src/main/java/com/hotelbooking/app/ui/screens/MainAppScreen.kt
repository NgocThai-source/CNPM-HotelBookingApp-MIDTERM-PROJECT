// ui/screens/profile/MainAppScreen.kt
package com.hotelbooking.app.ui.screens.profile
import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.home.HomeScreen
import com.hotelbooking.app.ui.screens.profile.ProfileScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.BookingHistoryItem


import com.hotelbooking.app.ui.navigation.NavGraph

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController = navController) }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("booking_history") {
            BookingHistoryItem(navController = navController)
        }

    }
    NavHost(navController = navController, startDestination = Routes.HOME){
        composable(Routes.HOME){
            HomeScreen(navController = navController)
        }
        composable(Routes.PROFILE){
            ProfileScreen(navController = navController)
        }
        composable("booking_history") {
            BookingHistoryItem(navController = navController)
        }
    }
}

