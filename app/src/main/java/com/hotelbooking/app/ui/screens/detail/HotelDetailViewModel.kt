package com.hotelbooking.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.data.model.Room
import com.hotelbooking.app.data.repository.AuthRepository
import com.hotelbooking.app.data.repository.ChatRepository
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
class HotelDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val hotelRepository: HotelRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val hotelId: String = savedStateHandle[Constants.ARG_HOTEL_ID] ?: ""

    private val _hotelState = MutableStateFlow<UiState<Hotel>>(UiState.Loading)
    val hotelState: StateFlow<UiState<Hotel>> = _hotelState.asStateFlow()

    private val _roomsState = MutableStateFlow<UiState<List<Room>>>(UiState.Loading)
    val roomsState: StateFlow<UiState<List<Room>>> = _roomsState.asStateFlow()

    init {
        loadHotelDetail()
        loadRooms()
    }

    private fun loadHotelDetail() {
        viewModelScope.launch {
            hotelRepository.getHotelById(hotelId)
                .onSuccess { _hotelState.value = UiState.Success(it) }
                .onFailure { _hotelState.value = UiState.Error(it.message ?: "Lỗi tải thông tin") }
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            hotelRepository.getRoomsFlow(hotelId)
                .catch { e -> _roomsState.value = UiState.Error(e.message ?: "Lỗi tải phòng") }
                .collect { rooms -> _roomsState.value = UiState.Success(rooms) }
        }
    }

    /** Start or get chat with the hotel manager */
    suspend fun startChat(managerId: String, managerName: String): Result<String> {
        val currentUser = authRepository.currentUser ?: return Result.failure(Exception("Not logged in"))
        val profile = authRepository.getUserProfile(currentUser.uid).getOrNull()
        return chatRepository.getOrCreateChat(
            userId = currentUser.uid,
            userName = profile?.displayName ?: "Khách",
            otherUserId = managerId,
            otherUserName = managerName
        )
    }
}
