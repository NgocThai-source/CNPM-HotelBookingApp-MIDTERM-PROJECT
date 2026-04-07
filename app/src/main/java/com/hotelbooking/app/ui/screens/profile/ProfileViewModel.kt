package com.hotelbooking.app.ui.screens.profile

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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val profileState: StateFlow<UiState<User>> = _profileState.asStateFlow()

    val isManager: Boolean
        get() = (_profileState.value as? UiState.Success)?.data?.role == UserRole.MANAGER

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            authRepository.getUserProfile(userId)
                .onSuccess { _profileState.value = UiState.Success(it) }
                .onFailure { _profileState.value = UiState.Error(it.message ?: "Lỗi tải hồ sơ") }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
