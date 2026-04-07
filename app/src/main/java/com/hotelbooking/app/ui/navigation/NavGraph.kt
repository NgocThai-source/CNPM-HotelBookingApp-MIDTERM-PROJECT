package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hotelbooking.app.R
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.auth.RegisterScreen
import com.hotelbooking.app.ui.screens.booking.BookingScreen
import com.hotelbooking.app.ui.screens.booking.MyBookingsScreen
import com.hotelbooking.app.ui.screens.chat.ChatDetailScreen
import com.hotelbooking.app.ui.screens.chat.ChatListScreen
import com.hotelbooking.app.ui.screens.detail.HotelDetailScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen
import com.hotelbooking.app.ui.screens.manager.AddHotelScreen
import com.hotelbooking.app.ui.screens.manager.ManagerDashboardScreen
import com.hotelbooking.app.ui.screens.profile.ProfileScreen
import com.hotelbooking.app.util.Constants

/**
 * Data class for bottom navigation items.
 */
data class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Bottom navigation items list.
 */
val bottomNavItems = listOf(
    BottomNavItem(Routes.Home.route, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.MyBookings.route, R.string.nav_bookings, Icons.Filled.Luggage, Icons.Outlined.Luggage),
    BottomNavItem(Routes.ChatList.route, R.string.nav_chat, Icons.Filled.Chat, Icons.Outlined.Chat),
    BottomNavItem(Routes.Profile.route, R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person),
)

/**
 * Main navigation composable with bottom navigation bar.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Routes that should show the bottom navigation bar
    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { it.route == dest.route }
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = stringResource(item.titleResId)
                                )
                            },
                            label = { Text(stringResource(item.titleResId)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            }
        ) {
            // === Auth ===
            composable(Routes.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.Register.route)
                    }
                )
            }

            composable(Routes.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // === Main Tabs ===
            composable(Routes.Home.route) {
                HomeScreen(
                    onHotelClick = { hotelId ->
                        navController.navigate(Routes.HotelDetail.createRoute(hotelId))
                    }
                )
            }

            composable(Routes.MyBookings.route) {
                MyBookingsScreen(
                    onBookingClick = { /* Navigate to booking detail if needed */ }
                )
            }

            composable(Routes.ChatList.route) {
                ChatListScreen(
                    onChatClick = { chatId ->
                        navController.navigate(Routes.ChatDetail.createRoute(chatId))
                    }
                )
            }

            composable(Routes.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToManager = {
                        navController.navigate(Routes.ManagerDashboard.route)
                    }
                )
            }

            // === Detail Screens ===
            composable(
                route = Routes.HotelDetail.route,
                arguments = listOf(navArgument(Constants.ARG_HOTEL_ID) { type = NavType.StringType })
            ) {
                HotelDetailScreen(
                    onBookClick = { hotelId, roomId ->
                        navController.navigate(Routes.Booking.createRoute(hotelId, roomId))
                    },
                    onChatClick = { chatId ->
                        navController.navigate(Routes.ChatDetail.createRoute(chatId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.Booking.route,
                arguments = listOf(
                    navArgument(Constants.ARG_HOTEL_ID) { type = NavType.StringType },
                    navArgument("roomId") { type = NavType.StringType }
                )
            ) {
                BookingScreen(
                    onBookingSuccess = {
                        navController.navigate(Routes.MyBookings.route) {
                            popUpTo(Routes.Home.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ChatDetail.route,
                arguments = listOf(navArgument(Constants.ARG_CHAT_ID) { type = NavType.StringType })
            ) {
                ChatDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // === Manager Screens ===
            composable(Routes.ManagerDashboard.route) {
                ManagerDashboardScreen(
                    onAddHotel = { navController.navigate(Routes.AddHotel.route) },
                    onEditHotel = { hotelId ->
                        navController.navigate(Routes.EditHotel.createRoute(hotelId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.AddHotel.route) {
                AddHotelScreen(
                    onSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EditHotel.route,
                arguments = listOf(navArgument(Constants.ARG_HOTEL_ID) { type = NavType.StringType })
            ) {
                AddHotelScreen(
                    onSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
