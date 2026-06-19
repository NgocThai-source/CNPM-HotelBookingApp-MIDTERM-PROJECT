package com.hotelbooking.app.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Room
import com.hotelbooking.app.service.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoomSelectionViewModel : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchRooms(hotelId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = RetrofitClient.roomApi.getRoomsByHotelId(hotelId)
                _rooms.value = response.data?.filter { it.isActive != false } ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load rooms"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
