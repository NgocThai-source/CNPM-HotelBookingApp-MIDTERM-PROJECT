package com.hotelbooking.app.data.repository

// Import toàn bộ các Model cần thiết
import com.hotelbooking.app.data.model.AuthResponse
import com.hotelbooking.app.data.model.ForgotPasswordRequest
import com.hotelbooking.app.data.model.LoginRequest
import com.hotelbooking.app.data.model.LoginResponse
import com.hotelbooking.app.data.model.RegisterRequest
import com.hotelbooking.app.data.model.RegisterResponse
import com.hotelbooking.app.data.model.ResetPasswordRequest
import com.hotelbooking.app.data.model.VerifyOtpRequest
import com.hotelbooking.app.data.model.VerifyResetRequest

// Import thư viện Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRepository {

    // --- LUỒNG XÁC THỰC CƠ BẢN ---

    // API Đăng ký
    @POST("api/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): RegisterResponse

    // API Đăng nhập và trả về token
    @POST("api/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse


    // --- LUỒNG QUÊN MẬT KHẨU ---

    // API Gửi yêu cầu quên mật khẩu (nhận OTP qua email)
    @POST("api/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): AuthResponse

    // API Xác thực OTP và đặt lại mật khẩu mới
    @POST("api/verify-otp")
    suspend fun verifyOTP(@Body request: VerifyOtpRequest): AuthResponse

    // API : Đổi mật khẩu mới
    @POST("api/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): AuthResponse
}