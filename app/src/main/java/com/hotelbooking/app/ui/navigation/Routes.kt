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
    const val DETAIL = "detail/{hotelId}"
    const val MY_BOOKINGS = "my_bookings"

    const val BOOKING = "booking/{hotelId}/{hotelTitle}/{hotelPrice}/{hotelImageUrl}/{checkInAvailable}/{checkOutAvailable}/{exchangeRate}"

    const val PAYMENT = "payment/{bookingId}/{hotelName}/{hotelImageUrl}/{guestName}/{phone}/{totalPriceUSD}/{totalPriceVND}/{checkInDate}/{checkOutDate}/{numberOfNights}"

    fun bookingRoute(
        hotelId: String,
        hotelTitle: String,
        hotelPrice: Double,
        hotelImageUrl: String,
        checkInAvailable: String,
        checkOutAvailable: String,
        exchangeRate: Double
    ): String {
        val encodedTitle = URLEncoder.encode(hotelTitle, "UTF-8")
        val encodedImageUrl = URLEncoder.encode(hotelImageUrl, "UTF-8")
        return "booking/$hotelId/$encodedTitle/$hotelPrice/$encodedImageUrl/$checkInAvailable/$checkOutAvailable/${exchangeRate.toInt()}"
    }

    fun decodeHotelTitle(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    fun decodeImageUrl(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    fun decodeParam(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
}
