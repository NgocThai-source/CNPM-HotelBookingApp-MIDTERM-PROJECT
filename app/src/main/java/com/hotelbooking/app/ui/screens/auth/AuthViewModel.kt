package com.hotelbooking.app.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.LoginRequest
import com.hotelbooking.app.data.model.RegisterRequest
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.launch

sealed class AuthState { // Định nghĩa các trạng thái cho AuthViewModel
    object Idle : AuthState() // Nằm im chờ đợi, lúc user gõ email, pass
    object Loading : AuthState() // Đang xử lý
    data class Success(val message: String) : AuthState() // Thành công và kèm theo message từ Node.js
    data class Error(val message: String) : AuthState() // Thất bại và kèm theo message từ Node.js
}

class AuthViewModel : ViewModel() {
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
                authState = AuthState.Error(e.message ?: "Đã xảy ra lỗi kết nối")
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
                authState = AuthState.Error(e.message ?: "Đã xảy ra lỗi kết nối")
                onResult(false)
            }
        }
    }

    // --- RESET TRẠNG THÁI ---
    fun resetState() {
        authState = AuthState.Idle
    }
}