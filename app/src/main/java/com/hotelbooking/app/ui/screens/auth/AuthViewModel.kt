package com.hotelbooking.app.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.ForgotPasswordRequest
import com.hotelbooking.app.data.model.VerifyResetRequest // Bạn cần tạo thêm Model này
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    // --- PHẦN LƯU TRỮ DỮ LIỆU TÍCH LŨY (Để dùng cho bước cuối) ---
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var newPassword by mutableStateOf("")

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

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

    // --- BƯỚC 3: GỌI TẠI MÀN HÌNH CREATENEWPASSWORD ---
    fun verifyAndResetPassword(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                // Backend yêu cầu cả 3: email, otp, newPassword
                val request = VerifyResetRequest(
                    email = email,
                    otp = otp,
                    newPassword = newPassword
                )
                val response = RetrofitClient.apiInterface.verifyAndResetPassword(request)

                if (response.success) {
                    authState = AuthState.Success(response.message)
                    onResult(true)
                } else {
                    authState = AuthState.Error(response.message)
                    onResult(false)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Reset password failed")
                onResult(false)
            }
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}