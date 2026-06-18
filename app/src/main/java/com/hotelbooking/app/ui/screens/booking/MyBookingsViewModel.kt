package com.hotelbooking.app.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.repository.BookingItem
import com.hotelbooking.app.data.repository.BookingListResponse
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyBookingsViewModel : ViewModel() {

    private val _bookings = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookings: StateFlow<List<BookingItem>> = _bookings

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error



    fun fetchBookings(userId: String) { // Thêm tham số userId
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Truyền userId vào API
                val response: BookingListResponse = RetrofitClient.bookingApi.getBookings(userId)
                if (response.success) {
                    _bookings.value = response.data ?: emptyList()
                } else {
                    _error.value = "Failed to load bookings"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
                _bookings.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
