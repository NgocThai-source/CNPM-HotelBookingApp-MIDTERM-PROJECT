package com.hotelbooking.app.data.repository

// Import toàn bộ các Model cần thiết
import com.hotelbooking.app.data.model.AuthResponse
import com.hotelbooking.app.data.model.ChangePasswordRequest
import com.hotelbooking.app.data.model.ForgotPasswordRequest
import com.hotelbooking.app.data.model.LoginRequest
import com.hotelbooking.app.data.model.LoginResponse
import com.hotelbooking.app.data.model.ProfileResponse
import com.hotelbooking.app.data.model.RegisterRequest
import com.hotelbooking.app.data.model.RegisterResponse
import com.hotelbooking.app.data.model.ResetPasswordRequest
import com.hotelbooking.app.data.model.UpdateProfileRequest
import com.hotelbooking.app.data.model.VerifyOtpRequest

// Import thư viện Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

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


    // --- PROFILE MANAGEMENT ---

    // API Lấy thông tin profile người dùng
    @GET("api/profile")
    suspend fun getProfile(): ProfileResponse

    // API Cập nhật profile (tên)
    @PUT("api/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ProfileResponse

    // API Đổi mật khẩu với mật khẩu cũ
    @PUT("api/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): AuthResponse
}