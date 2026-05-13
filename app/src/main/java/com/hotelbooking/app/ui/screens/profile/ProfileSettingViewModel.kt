package com.hotelbooking.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.ChangePasswordRequest
import com.hotelbooking.app.data.model.ProfileResponse
import com.hotelbooking.app.data.model.UpdateProfileRequest
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: ProfileData? = null,
    val error: String? = null,
    val nameUpdateSuccess: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val passwordChangeError: String? = null
)

data class ProfileData(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val phone: String = ""
)

class ProfileSettingViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.authApi.getProfile()
                if (response.success && response.data != null) {
                    val d = response.data
                    _state.value = _state.value.copy(
                        isLoading = false,
                        profile = ProfileData(
                            userId = d.userId,
                            email = d.email,
                            fullName = d.fullName,
                            phone = d.phone
                        )
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = response.message
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun updateName(newName: String) {
        if (newName.isBlank()) {
            _state.value = _state.value.copy(error = "Name cannot be empty")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, nameUpdateSuccess = false)
            try {
                val response = RetrofitClient.authApi.updateProfile(UpdateProfileRequest(fullName = newName))
                if (response.success && response.data != null) {
                    val d = response.data
                    _state.value = _state.value.copy(
                        isLoading = false,
                        profile = ProfileData(
                            userId = d.userId,
                            email = d.email,
                            fullName = d.fullName,
                            phone = d.phone
                        ),
                        nameUpdateSuccess = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = response.message
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update name"
                )
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        if (oldPassword.isBlank()) {
            _state.value = _state.value.copy(passwordChangeError = "Old password is required")
            return
        }
        if (newPassword.length < 6) {
            _state.value = _state.value.copy(passwordChangeError = "New password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, passwordChangeError = null, passwordChangeSuccess = false)
            try {
                val response = RetrofitClient.authApi.changePassword(
                    ChangePasswordRequest(oldPassword = oldPassword, newPassword = newPassword)
                )
                if (response.success) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        passwordChangeSuccess = true,
                        passwordChangeError = null
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        passwordChangeError = response.message
                    )
                }
            } catch (e: Exception) {
                val message = e.message ?: "Failed to change password"
                _state.value = _state.value.copy(
                    isLoading = false,
                    passwordChangeError = message
                )
            }
        }
    }

    fun clearPasswordState() {
        _state.value = _state.value.copy(passwordChangeSuccess = false, passwordChangeError = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null, nameUpdateSuccess = false)
    }
}
