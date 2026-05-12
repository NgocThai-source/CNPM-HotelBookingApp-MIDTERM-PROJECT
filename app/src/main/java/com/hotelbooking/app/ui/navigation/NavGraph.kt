package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.hotelbooking.app.ui.screens.profile.ProfileScreen
import androidx.navigation.NavController
import androidx.compose.material3.Text
import com.hotelbooking.app.ui.screens.profile.Routes
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.BookingHistoryItem
import com.hotelbooking.app.ui.screens.chat.ChatDetailScreen
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.PaymentItem
import kotlin.collections.emptyList
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.ProfileSettingItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color



@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeIn(tween(animationDuration))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeOut(tween(animationDuration))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeIn(tween(animationDuration))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeOut(tween(animationDuration))
        }
    ) {

        composable(Routes.LOGIN) {

            LoginScreen(
                onLoginClick = {
                    navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                navController = navController
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                navController = navController
            )
        }
        composable(Routes.BOOKING_HISTORY) {
            BookingHistoryItem(
                navController = navController,
                bookingList = emptyList()
            )
        }
        composable(Routes.PAYMENT_METHOD) {
            PaymentItem(
                navController = navController
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                navController = navController
            )
        }
    }
}

@Composable
fun EditProfileScreen(navController: NavController) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {

        ProfileSettingItem(
            fullName = "kiet",
            email = "kietlv.24it@vku.udn.vn",
            phone = "0123456",
            createdDate = "12/05/2026",
            password = "12345678"
        )
    }
}