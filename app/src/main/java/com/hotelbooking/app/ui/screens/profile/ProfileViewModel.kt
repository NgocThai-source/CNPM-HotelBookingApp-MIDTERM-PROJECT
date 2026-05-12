package com.hotelbooking.app.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Trạng thái của màn hình (Đang tải, Thành công, Lỗi)
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: ProfileUser) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
        private set

    private val apiService = ApiService.create()

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            uiState = ProfileUiState.Loading
            try {
                // Gọi API
                val response = apiService.getUserProfile(userId)
                if (response.success) {
                    uiState = ProfileUiState.Success(response.data)
                } else {
                    uiState = ProfileUiState.Error("Dữ liệu lỗi từ server")
                }
            } catch (e: Exception) {
                uiState = ProfileUiState.Error(e.message ?: "Không thể kết nối đến Server")
            }
        }
    }
}