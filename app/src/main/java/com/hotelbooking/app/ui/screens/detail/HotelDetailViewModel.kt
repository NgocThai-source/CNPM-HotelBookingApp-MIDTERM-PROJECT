package com.hotelbooking.app.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.launch

sealed class HotelDetailState {
    object Loading : HotelDetailState()
    data class Success(val hotel: Hotel) : HotelDetailState()
    data class Error(val message: String) : HotelDetailState()
}

class HotelDetailViewModel : ViewModel() {
    var detailState by mutableStateOf<HotelDetailState>(HotelDetailState.Loading)
        private set

    fun fetchHotelDetail(hotelId: String) {
        viewModelScope.launch {
            detailState = HotelDetailState.Loading
            try {
                // Gọi API lấy dữ liệu từ Supabase/Backend
                val hotel = RetrofitClient.hotelApi.getHotelById(hotelId)
                detailState = HotelDetailState.Success(hotel)
            } catch (e: Exception) {
                detailState = HotelDetailState.Error(e.message ?: "Lỗi tải chi tiết khách sạn")
            }
        }
    }
}
