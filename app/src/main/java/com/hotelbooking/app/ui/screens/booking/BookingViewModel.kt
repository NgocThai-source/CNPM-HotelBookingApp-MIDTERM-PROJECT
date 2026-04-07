package com.hotelbooking.app.ui.screens.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Booking
import com.hotelbooking.app.data.repository.AuthRepository
import com.hotelbooking.app.data.repository.BookingRepository
import com.hotelbooking.app.data.repository.HotelRepository
import com.hotelbooking.app.util.Constants
import com.hotelbooking.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookingRepository: BookingRepository,
    private val hotelRepository: HotelRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _bookingState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val bookingState: StateFlow<UiState<String>> = _bookingState.asStateFlow()

    private val _userBookings = MutableStateFlow<UiState<List<Booking>>>(UiState.Loading)
    val userBookings: StateFlow<UiState<List<Booking>>> = _userBookings.asStateFlow()

    init {
        loadUserBookings()
    }

    /** Load current user's bookings */
    private fun loadUserBookings() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            bookingRepository.getUserBookingsFlow(userId)
                .catch { e -> _userBookings.value = UiState.Error(e.message ?: "Lỗi") }
                .collect { _userBookings.value = UiState.Success(it) }
        }
    }

    /** Create a new booking */
    fun createBooking(booking: Booking) {
        viewModelScope.launch {
            _bookingState.value = UiState.Loading
            bookingRepository.createBooking(booking)
                .onSuccess { _bookingState.value = UiState.Success(it) }
                .onFailure { _bookingState.value = UiState.Error(it.message ?: "Đặt phòng thất bại") }
        }
    }

    /** Cancel a booking */
    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId)
        }
    }
}
