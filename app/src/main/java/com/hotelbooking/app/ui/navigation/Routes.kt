package com.hotelbooking.app.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFY_OTP = "verify_otp"
    const val RESET_PASSWORD = "reset_password"

    // Đảm bảo có 2 constant này
    const val DETAIL = "detail/{hotelName}"
    fun detailRoute(hotelName: String) = "detail/$hotelName"
}
