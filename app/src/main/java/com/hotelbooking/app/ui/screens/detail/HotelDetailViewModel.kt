package com.hotelbooking.app.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.data.model.Review
import com.hotelbooking.app.service.RetrofitClient
import com.hotelbooking.app.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HotelDetailState {
    object Loading : HotelDetailState()
    data class Success(val hotel: Hotel) : HotelDetailState()
    data class Error(val message: String) : HotelDetailState()
}

class HotelDetailViewModel : ViewModel() {
    var detailState by mutableStateOf<HotelDetailState>(HotelDetailState.Loading)
        private set

    private val _exchangeRate = MutableStateFlow(26000.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _hasUserReviewed = MutableStateFlow(false)
    val hasUserReviewed: StateFlow<Boolean> = _hasUserReviewed

    private val _isSubmittingReview = MutableStateFlow(false)
    val isSubmittingReview: StateFlow<Boolean> = _isSubmittingReview

    private val _averageRating = MutableStateFlow(0.0)
    val averageRating: StateFlow<Double> = _averageRating

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> = _reviewCount

    init {
        fetchSettings()
    }

    fun fetchSettings() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.settingsApi.getSettings()
                if (response.success && response.data != null) {
                    response.data["exchange_rate_usd_to_vnd"]?.toDoubleOrNull()?.let {
                        _exchangeRate.value = it
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun fetchHotelDetail(hotelId: String) {
        viewModelScope.launch {
            detailState = HotelDetailState.Loading
            try {
                val response = RetrofitClient.hotelApi.getHotelById(hotelId)
                if (response.success) {
                    detailState = HotelDetailState.Success(response.data)
                    _averageRating.value = response.data.rating
                    _reviewCount.value = response.data.reviewCount
                } else {
                    detailState = HotelDetailState.Error("Failed to load hotel details")
                }
            } catch (e: Exception) {
                detailState = HotelDetailState.Error(e.message ?: "Failed to load hotel details")
            }
        }

        fetchReviews(hotelId)
        checkUserReviewStatus(hotelId)
    }

    private fun fetchReviews(hotelId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.reviewApi.getReviewsByHotelId(hotelId)
                if (response.success) {
                    _reviews.value = response.data ?: emptyList()
                    response.rating?.let { _averageRating.value = it }
                    response.reviewCount?.let { _reviewCount.value = it }
                }
            } catch (_: Exception) { }
        }
    }

    fun checkUserReviewStatus(hotelId: String) {
        val token = TokenManager.getToken() ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.reviewApi.getUserReviewStatus(hotelId, TokenManager.getAuthHeader())
                if (response.success) {
                    _hasUserReviewed.value = response.hasReviewed == true
                }
            } catch (_: Exception) { }
        }
    }

    fun submitReview(hotelId: String, content: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = TokenManager.getToken()
        if (token == null) {
            onError("Please login to submit a review")
            return
        }

        viewModelScope.launch {
            _isSubmittingReview.value = true
            try {
                val response = RetrofitClient.reviewApi.createReview(
                    hotelId = hotelId,
                    token = TokenManager.getAuthHeader(),
                    body = mapOf("content" to content)
                )
                if (response.success && response.data != null) {
                    _reviews.value = listOf(response.data) + _reviews.value
                    _hasUserReviewed.value = true
                    _reviewCount.value = _reviewCount.value + 1
                    onSuccess()
                } else {
                    onError(response.message ?: "Failed to submit review")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to submit review")
            } finally {
                _isSubmittingReview.value = false
            }
        }
    }
}
