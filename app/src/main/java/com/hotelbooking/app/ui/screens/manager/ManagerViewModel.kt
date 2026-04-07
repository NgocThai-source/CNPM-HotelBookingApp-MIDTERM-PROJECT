package com.hotelbooking.app.ui.screens.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.data.repository.AuthRepository
import com.hotelbooking.app.data.repository.HotelRepository
import com.hotelbooking.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _managedHotels = MutableStateFlow<UiState<List<Hotel>>>(UiState.Loading)
    val managedHotels: StateFlow<UiState<List<Hotel>>> = _managedHotels.asStateFlow()

    private val _addHotelState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val addHotelState: StateFlow<UiState<String>> = _addHotelState.asStateFlow()

    init {
        loadManagedHotels()
    }

    private fun loadManagedHotels() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            hotelRepository.getHotelsByManagerFlow(userId)
                .catch { e -> _managedHotels.value = UiState.Error(e.message ?: "Lỗi") }
                .collect { _managedHotels.value = UiState.Success(it) }
        }
    }

    fun addHotel(hotel: Hotel) {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _addHotelState.value = UiState.Loading
            hotelRepository.addHotel(hotel.copy(managerId = userId))
                .onSuccess { _addHotelState.value = UiState.Success(it) }
                .onFailure { _addHotelState.value = UiState.Error(it.message ?: "Lỗi thêm khách sạn") }
        }
    }

    fun deleteHotel(hotelId: String) {
        viewModelScope.launch {
            hotelRepository.deleteHotel(hotelId)
        }
    }
}
