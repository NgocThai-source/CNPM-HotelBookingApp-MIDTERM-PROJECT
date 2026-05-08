package com.hotelbooking.app.data.model
data class VerifyOtpRequest(val email: String, val otp: String)
data class ResetPasswordRequest(val email: String, val newPassword: String)

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


// Model cho bước 1:Gửi mail
data class ForgotPasswordRequest(
    val email: String
)

// Model cho bước 3:Gửi 3 thứ cùng lúc (Email + OTP + Pass mới)
data class VerifyResetRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

// Model chung cho kết quả trả về từ Backend
data class AuthResponse(
    val success: Boolean,
    val message: String
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String
)

