package com.hotelbooking.app.data.model
data class LoginRequest(
    val email: String,
    val password: String
)

// Dữ liệu server trả về khi Đăng nhập
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val data: UserData? = null
)
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)
data class UserData(
    val userId: String,
    val email: String,
    val fullName: String,
    val phone: String,
)