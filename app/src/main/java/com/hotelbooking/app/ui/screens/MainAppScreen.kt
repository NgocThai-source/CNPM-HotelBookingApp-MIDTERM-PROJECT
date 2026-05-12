package com.hotelbooking.app.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.hotelbooking.app.ui.screens.home.HomeScreen
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.BookingHistoryItem
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.ProfileSettingItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("booking_history") {
            BookingHistoryItem(
                navController = navController,
                bookingList = emptyList()
            )
        }
        composable("profile_setting"){
            ProfileScreen(navController = navController)
        }
    }
}