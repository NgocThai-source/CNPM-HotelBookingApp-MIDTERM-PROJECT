package com.hotelbooking.app.ui.navigation

import java.net.URLEncoder
import java.net.URLDecoder

object Routes {
    const val LOGIN = "login"

    const val REGISTER = "register"

    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFY_OTP = "verify_otp"
    const val RESET_PASSWORD = "reset_password"

    const val HOME = "home"
    const val PROFILE_SETTING = "profile_setting"
    const val DETAIL = "detail/{hotelId}"
    const val MY_BOOKINGS = "my_bookings"
    const val NOTIFICATIONS = "notifications"
    const val CHAT_LIST = "chat_list"
    const val CHAT_DETAIL = "chat_detail/{conversationId}/{participantName}"

    const val ROOM_SELECT = "room_select/{hotelId}/{hotelTitle}/{hotelImageUrl}/{exchangeRate}"

    const val BOOKING = "booking/{hotelId}/{hotelTitle}/{hotelPrice}/{hotelImageUrl}/{checkInAvailable}/{checkOutAvailable}/{exchangeRate}/{roomId}/{roomType}"

    const val PAYMENT = "payment/{bookingId}/{hotelName}/{hotelImageUrl}/{guestName}/{phone}/{totalPriceUSD}/{totalPriceVND}/{checkInDate}/{checkOutDate}/{numberOfNights}"

    fun roomSelectRoute(
        hotelId: String,
        hotelTitle: String,
        hotelImageUrl: String,
        exchangeRate: Double
    ): String {
        val encodedTitle = URLEncoder.encode(hotelTitle, "UTF-8")
        val encodedImageUrl = URLEncoder.encode(hotelImageUrl, "UTF-8")
        return "room_select/$hotelId/$encodedTitle/$encodedImageUrl/${exchangeRate.toInt()}"
    }

    fun bookingRoute(
        hotelId: String,
        hotelTitle: String,
        hotelPrice: Double,
        hotelImageUrl: String,
        checkInAvailable: String,
        checkOutAvailable: String,
        exchangeRate: Double,
        roomId: String = "none",
        roomType: String = ""
    ): String {
        val encodedTitle = URLEncoder.encode(hotelTitle, "UTF-8")
        val encodedImageUrl = URLEncoder.encode(hotelImageUrl, "UTF-8")
        val encodedRoomType = URLEncoder.encode(roomType.ifEmpty { "Standard" }, "UTF-8")
        val safeRoomId = roomId.ifEmpty { "none" }
        return "booking/$hotelId/$encodedTitle/$hotelPrice/$encodedImageUrl/$checkInAvailable/$checkOutAvailable/${exchangeRate.toInt()}/$safeRoomId/$encodedRoomType"
    }

    fun decodeHotelTitle(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    fun decodeImageUrl(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    fun decodeParam(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")

    fun chatDetailRoute(conversationId: String, participantName: String): String {
        val encodedName = URLEncoder.encode(participantName, "UTF-8")
        return "chat_detail/$conversationId/$encodedName"
    }
}
