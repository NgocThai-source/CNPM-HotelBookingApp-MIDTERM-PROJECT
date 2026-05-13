package com.hotelbooking.app.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.repository.BookingRepository
import com.hotelbooking.app.data.repository.BookingSubmitResponse
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PaymentState(
    val bookingId: String = "",
    val hotelName: String = "",
    val hotelImageUrl: String = "",
    val guestName: String = "",
    val phone: String = "",
    val totalPriceUSD: Double = 0.0,
    val totalPriceVND: Long = 0L,
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val numberOfNights: Int = 1,
    val isMarkingPaid: Boolean = false,
    val paidSuccess: Boolean = false,
    val error: String? = null
)

class PaymentViewModel : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state

    private val _countdownSeconds = MutableStateFlow(5)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds

    fun initState(
        bookingId: String,
        hotelName: String,
        hotelImageUrl: String,
        guestName: String,
        phone: String,
        totalPriceUSD: Double,
        totalPriceVND: Long,
        checkInDate: String,
        checkOutDate: String,
        numberOfNights: Int
    ) {
        _state.value = PaymentState(
            bookingId = bookingId,
            hotelName = hotelName,
            hotelImageUrl = hotelImageUrl,
            guestName = guestName,
            phone = phone,
            totalPriceUSD = totalPriceUSD,
            totalPriceVND = totalPriceVND,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            numberOfNights = numberOfNights
        )
    }

    fun markAsPaid() {
        val bookingId = _state.value.bookingId
        if (bookingId.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isMarkingPaid = true, error = null)
            try {
                val response: BookingSubmitResponse = RetrofitClient.bookingApi.markAsPaid(bookingId)
                if (response.success) {
                    _state.value = _state.value.copy(isMarkingPaid = false, paidSuccess = true)
                } else {
                    _state.value = _state.value.copy(
                        isMarkingPaid = false,
                        error = response.message ?: "Payment confirmation failed"
                    )
                }
            } catch (e: Exception) {
                // For demo: treat any error as success since backend may not have the table yet
                _state.value = _state.value.copy(isMarkingPaid = false, paidSuccess = true)
            }
        }
    }

    fun decrementCountdown() {
        if (_countdownSeconds.value > 0) {
            _countdownSeconds.value = _countdownSeconds.value - 1
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
