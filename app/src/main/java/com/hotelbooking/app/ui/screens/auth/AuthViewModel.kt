package com.hotelbooking.app.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.ForgotPasswordRequest
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.launch


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    fun forgotPassword(request: ForgotPasswordRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = RetrofitClient.apiInterface.forgotPassword(request)
                if (response.success) {
                    authState = AuthState.Success(response.message)
                    onResult(true)
                } else {
                    authState = AuthState.Error(response.message)
                    onResult(false)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Lỗi kết nối server")
                onResult(false)
            }
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}