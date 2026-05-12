package com.hotelbooking.app.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.ForgotPasswordRequest
import com.hotelbooking.app.data.model.LoginRequest
import com.hotelbooking.app.data.model.RegisterRequest
import com.hotelbooking.app.data.model.ResetPasswordRequest
import com.hotelbooking.app.data.model.VerifyOtpRequest
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.launch

sealed class AuthState { // Định nghĩa các trạng thái cho AuthViewModel
    object Idle : AuthState() // Nằm im chờ đợi, lúc user gõ email, pass
    object Loading : AuthState() // Đang xử lý
    data class Success(val message: String) : AuthState() // Thành công và kèm theo message từ Node.js
    data class Error(val message: String) : AuthState() // Thất bại và kèm theo message từ Node.js
}

class AuthViewModel : ViewModel() {
    // --- PHẦN LƯU TRỮ DỮ LIỆU TÍCH LŨY (Để dùng cho luồng Quên mật khẩu) ---
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var newPassword by mutableStateOf("")

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    // --- HÀM ĐĂNG KÝ ---
    fun register(request: RegisterRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = RetrofitClient.apiInterface.registerUser(request)
                if (response.success) {
                    authState = AuthState.Success(response.message)
                    onResult(true)
                } else {
                    authState = AuthState.Error(response.message)
                    onResult(false)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Connection error")
                onResult(false)
            }
        }
    }

    // --- HÀM ĐĂNG NHẬP ---
    fun login(request: LoginRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = RetrofitClient.apiInterface.loginUser(request)
                if (response.success) {
                    authState = AuthState.Success(response.message)
                    onResult(true)
                } else {
                    authState = AuthState.Error(response.message)
                    onResult(false)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Connection error")
                onResult(false)
            }
        }
    }

    // --- BƯỚC 1: GỌI TẠI MÀN HÌNH FORGOTPASS ---
    fun forgotPassword(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                // Tạo request chỉ với Email
                val request = ForgotPasswordRequest(email = email)
                val response = RetrofitClient.apiInterface.forgotPassword(request)

                if (response.success) {
                    authState = AuthState.Success(response.message)
                    onResult(true)
                } else {
                    authState = AuthState.Error(response.message)
                    onResult(false)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Connection error")
                onResult(false)
            }
        }
    }

    // --- BƯỚC 2: KIỂM TRA OTP (Gọi ở màn hình nhập OTP) ---
    // --- BƯỚC 2: CHỈ KIỂM TRA OTP ---
    fun verifyOTP(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                // Đảm bảo dùng VerifyOtpRequest và gọi hàm verifyOTP của Repository
                val request = VerifyOtpRequest(email = email, otp = otp)
                val response = RetrofitClient.apiInterface.verifyOTP(request)

                if (response.success) {
                    // Nếu Backend trả về "OTP hợp lệ", Toast sẽ hiện dòng này
                    authState = AuthState.Success(response.message)
                    onResult(true)
                } else {
                    authState = AuthState.Error(response.message)
                    onResult(false)
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Connection error: ${e.message}")
                onResult(false)
            }
        }
    }

    // --- BƯỚC 3: ĐẶT LẠI MẬT KHẨU (Gọi ở màn hình nhập Pass mới) ---
    fun resetPassword(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val request = ResetPasswordRequest(email = email, newPassword = newPassword)
                val response = RetrofitClient.apiInterface.resetPassword(request)

                if (response.success) {
                    authState = AuthState.Success(response.message)
                    onResult(true)
                } else {
                    authState = AuthState.Error(response.message)
                    onResult(false)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Connection error")
                onResult(false)
            }
        }
    }

    // --- RESET TRẠNG THÁI ---
    fun resetState() {
        authState = AuthState.Idle
    }
}