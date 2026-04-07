package com.hotelbooking.app.ui.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Routes(val route: String) {
    // Auth
    data object Login : Routes("login")
    data object Register : Routes("register")

    // Main tabs
    data object Home : Routes("home")
    data object MyBookings : Routes("my_bookings")
    data object ChatList : Routes("chat_list")
    data object Profile : Routes("profile")

    // Detail screens
    data object HotelDetail : Routes("hotel_detail/{hotelId}") {
        fun createRoute(hotelId: String) = "hotel_detail/$hotelId"
    }

    data object Booking : Routes("booking/{hotelId}/{roomId}") {
        fun createRoute(hotelId: String, roomId: String) = "booking/$hotelId/$roomId"
    }

    data object ChatDetail : Routes("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }

    // Manager screens
    data object ManagerDashboard : Routes("manager_dashboard")
    data object AddHotel : Routes("add_hotel")
    data object EditHotel : Routes("edit_hotel/{hotelId}") {
        fun createRoute(hotelId: String) = "edit_hotel/$hotelId"
    }
}
