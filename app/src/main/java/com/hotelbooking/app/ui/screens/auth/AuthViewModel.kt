package com.hotelbooking.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.User
import com.hotelbooking.app.data.model.UserRole
import com.hotelbooking.app.data.repository.AuthRepository
import com.hotelbooking.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Login and Register screens.
 * Handles authentication logic and state management.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loginState: StateFlow<UiState<Unit>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val registerState: StateFlow<UiState<User>> = _registerState.asStateFlow()

    /** Check if user is already logged in */
    val isLoggedIn: Boolean get() = authRepository.currentUser != null

    /**
     * Login with email and password.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Vui lòng nhập đầy đủ email và mật khẩu")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            authRepository.login(email, password)
                .onSuccess { _loginState.value = UiState.Success(Unit) }
                .onFailure { _loginState.value = UiState.Error(it.message ?: "Đăng nhập thất bại") }
        }
    }

    /**
     * Register a new user.
     */
    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String,
        phoneNumber: String,
        role: UserRole
    ) {
        // Validation
        when {
            email.isBlank() || password.isBlank() || displayName.isBlank() -> {
                _registerState.value = UiState.Error("Vui lòng nhập đầy đủ thông tin")
                return
            }
            password.length < 6 -> {
                _registerState.value = UiState.Error("Mật khẩu phải có ít nhất 6 ký tự")
                return
            }
            password != confirmPassword -> {
                _registerState.value = UiState.Error("Mật khẩu xác nhận không khớp")
                return
            }
        }

        viewModelScope.launch {
            _registerState.value = UiState.Loading
            authRepository.register(email, password, displayName, phoneNumber, role)
                .onSuccess { _registerState.value = UiState.Success(it) }
                .onFailure { _registerState.value = UiState.Error(it.message ?: "Đăng ký thất bại") }
        }
    }

    /** Reset login state */
    fun resetLoginState() { _loginState.value = UiState.Idle }

    /** Reset register state */
    fun resetRegisterState() { _registerState.value = UiState.Idle }
}
