package com.hotelbooking.app.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.repository.BookingSubmitRequest
import com.hotelbooking.app.data.repository.BookingSubmitResponse
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BookingFormState(
    val hotelId: String = "",
    val hotelTitle: String = "",
    val hotelPrice: Double = 0.0,
    val hotelImageUrl: String = "",
    val checkInAvailable: String = "",
    val checkOutAvailable: String = "",
    val roomId: String = "",
    val roomType: String = "",
    val guestName: String = "",
    val phone: String = "",
    val checkInDate: Long? = null,
    val checkOutDate: Long? = null,
    val guestCount: Int = 1,
    val exchangeRate: Double = 26000.0,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val submitError: String? = null,
    val bookingId: String? = null
)

class BookingViewModel : ViewModel() {

    private val _formState = MutableStateFlow(BookingFormState())
    val formState: StateFlow<BookingFormState> = _formState

    val numberOfNights: Int
        get() {
            val checkIn = _formState.value.checkInDate ?: return 1
            val checkOut = _formState.value.checkOutDate ?: return 1
            val diff = checkOut - checkIn
            return if (diff <= 0) 1 else (diff / (1000 * 60 * 60 * 24)).toInt()
        }

    val totalPriceUSD: Double
        get() = _formState.value.hotelPrice * numberOfNights

    val totalPriceVND: Long
        get() = (totalPriceUSD * _formState.value.exchangeRate).toLong()

    val isFormValid: Boolean
        get() {
            val state = _formState.value
            return state.guestName.isNotBlank() &&
                    state.phone.isNotBlank() &&
                    state.checkInDate != null &&
                    state.checkOutDate != null &&
                    state.checkOutDate > state.checkInDate
        }

    fun initWithHotelData(
        hotelId: String,
        hotelTitle: String,
        hotelPrice: Double,
        hotelImageUrl: String,
        checkInAvailable: String,
        checkOutAvailable: String,
        exchangeRate: Double,
        roomId: String = "",
        roomType: String = ""
    ) {
        _formState.value = BookingFormState(
            hotelId = hotelId,
            hotelTitle = hotelTitle,
            hotelPrice = hotelPrice,
            hotelImageUrl = hotelImageUrl,
            checkInAvailable = checkInAvailable,
            checkOutAvailable = checkOutAvailable,
            exchangeRate = exchangeRate,
            roomId = roomId,
            roomType = roomType
        )
    }

    fun updateGuestName(name: String) {
        _formState.value = _formState.value.copy(guestName = name, submitError = null)
    }

    fun updatePhone(phone: String) {
        _formState.value = _formState.value.copy(phone = phone, submitError = null)
    }

    fun updateCheckInDate(dateMillis: Long) {
        val currentOut = _formState.value.checkOutDate
        val newCheckOut = if (currentOut != null && currentOut <= dateMillis) null else currentOut
        _formState.value = _formState.value.copy(checkInDate = dateMillis, checkOutDate = newCheckOut, submitError = null)
    }

    fun updateCheckOutDate(dateMillis: Long) {
        _formState.value = _formState.value.copy(checkOutDate = dateMillis, submitError = null)
    }

    fun updateGuestCount(count: Int) {
        _formState.value = _formState.value.copy(guestCount = count.coerceAtLeast(1))
    }

    fun updateExchangeRate(rate: Double) {
        _formState.value = _formState.value.copy(exchangeRate = rate)
    }

    fun submitBooking(userId: String) {
        val state = _formState.value
        if (!isFormValid) return

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSubmitting = true, submitError = null)
            try {
                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                val request = BookingSubmitRequest(
                    userId = userId,
                    hotelId = state.hotelId,
                    hotelTitle = state.hotelTitle,
                    hotelImageUrl = state.hotelImageUrl,
                    guestName = state.guestName.trim(),
                    phone = state.phone.trim(),
                    checkInDate = dateFormat.format(java.util.Date(state.checkInDate!!)),
                    checkOutDate = dateFormat.format(java.util.Date(state.checkOutDate!!)),
                    numberOfNights = numberOfNights,
                    guestCount = state.guestCount,
                    pricePerNight = state.hotelPrice,
                    totalPrice = totalPriceUSD,
                    exchangeRate = state.exchangeRate,
                    roomId = state.roomId.takeIf { it.isNotBlank() && it != "none" },
                    roomType = state.roomType.takeIf { it.isNotBlank() }
                )
                val response: BookingSubmitResponse = RetrofitClient.bookingApi.submitBooking(request)
                if (response.success) {
                    _formState.value = _formState.value.copy(
                        isSubmitting = false,
                        submitSuccess = true,
                        bookingId = response.data?.bookingId ?: "BK-XXXXX"
                    )
                } else {
                    _formState.value = _formState.value.copy(
                        isSubmitting = false,
                        submitError = response.message ?: "Booking failed. Please try again."
                    )
                }
            } catch (e: Exception) {
                _formState.value = _formState.value.copy(
                    isSubmitting = false,
                    submitError = e.message ?: "Network error. Please check your connection."
                )
            }
        }
    }

    fun clearError() {
        _formState.value = _formState.value.copy(submitError = null)
    }
}
