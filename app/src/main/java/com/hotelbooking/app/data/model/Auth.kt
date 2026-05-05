package com.hotelbooking.app.data.model

// Model cho bước 1: Gửi mail
data class ForgotPasswordRequest(
    val email: String
)

// Model cho bước 3: Gửi 3 thứ cùng lúc (Email + OTP + Pass mới)
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
