package com.hotelbooking.app.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Notification
import com.hotelbooking.app.data.repository.NotificationRepository
import com.hotelbooking.app.service.RetrofitClient
import com.hotelbooking.app.service.SSEService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val notificationApi = RetrofitClient.notificationApi

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentUserId: String = ""

    companion object {
        val sharedUnreadCount = MutableStateFlow(0)
    }

    init {
        observeSSEEvents()
    }

    fun setUserId(userId: String) {
        if (currentUserId != userId) {
            currentUserId = userId
            fetchNotifications()
            fetchUnreadCount()
        }
    }

    private fun observeSSEEvents() {
        viewModelScope.launch {
            SSEService.events.collect { event ->
                when (event.event) {
                    "booking_confirmed", "admin_notification" -> {
                        if (currentUserId.isNotEmpty()) {
                            fetchNotifications()
                            fetchUnreadCount()
                        }
                    }
                }
            }
        }
    }

    fun fetchNotifications() {
        if (currentUserId.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = notificationApi.getNotifications(currentUserId)
                if (response.success) {
                    _notifications.value = response.data ?: emptyList()
                } else {
                    _error.value = "Failed to load notifications"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUnreadCount() {
        if (currentUserId.isEmpty()) return
        viewModelScope.launch {
            try {
                val response = notificationApi.getUnreadCount(currentUserId)
                if (response.success) {
                    _unreadCount.value = response.count
                    sharedUnreadCount.value = response.count
                }
            } catch (_: Exception) {
                // Silently fail for count
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            try {
                val response = notificationApi.markAsRead(notificationId)
                if (response.success) {
                    _notifications.value = _notifications.value.map {
                        if (it.id == notificationId) it.copy(is_read = true) else it
                    }
                    _unreadCount.value = maxOf(0, _unreadCount.value - 1)
                    sharedUnreadCount.value = maxOf(0, sharedUnreadCount.value - 1)
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    fun markAllAsRead() {
        if (currentUserId.isEmpty()) return
        viewModelScope.launch {
            try {
                val response = notificationApi.markAllAsRead(currentUserId)
                if (response.success) {
                    _notifications.value = _notifications.value.map { it.copy(is_read = true) }
                    _unreadCount.value = 0
                    sharedUnreadCount.value = 0
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            try {
                val response = notificationApi.deleteNotification(notificationId)
                if (response.success) {
                    val deleted = _notifications.value.find { it.id == notificationId }
                    _notifications.value = _notifications.value.filter { it.id != notificationId }
                    if (deleted?.is_read == false) {
                        _unreadCount.value = maxOf(0, _unreadCount.value - 1)
                        sharedUnreadCount.value = maxOf(0, sharedUnreadCount.value - 1)
                    }
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }
}
