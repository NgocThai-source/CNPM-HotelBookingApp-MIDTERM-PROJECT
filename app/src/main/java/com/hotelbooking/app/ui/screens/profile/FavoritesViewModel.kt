package com.hotelbooking.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.service.RetrofitClient
import com.hotelbooking.app.service.SSEService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class FavoritesState(
    val isLoading: Boolean = false,
    val favorites: List<Hotel> = emptyList(),
    val error: String? = null,
    val actionMessage: String? = null
)

/**
 * Singleton ViewModel shared between HomeScreen and ProfileSettingScreen.
 * Lives for the entire app session so favorites state persists across navigation.
 */
object FavoritesViewModel : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state

    private var sseListeningStarted = false

    init {
        fetchFavorites()
        startSseListening()
    }

    private fun startSseListening() {
        if (sseListeningStarted) return
        sseListeningStarted = true

        SSEService.events
            .onEach { event ->
                when (event.event) {
                    "favorite_added", "favorite_removed" -> fetchFavorites()
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchFavorites() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.hotelApi.getFavorites()
                if (response.success) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        favorites = response.data ?: emptyList()
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to load favorites"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load favorites"
                )
            }
        }
    }

    fun addFavorite(hotelId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.hotelApi.addFavorite(hotelId)
                if (response.success) {
                    fetchFavorites()
                    _state.value = _state.value.copy(actionMessage = "Added to favorites")
                } else {
                    _state.value = _state.value.copy(actionMessage = response.message)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    actionMessage = "Failed to add favorite: ${e.message}"
                )
            }
        }
    }

    fun removeFavorite(hotelId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.hotelApi.removeFavorite(hotelId)
                if (response.success) {
                    _state.value = _state.value.copy(
                        favorites = _state.value.favorites.filter { it.id != hotelId },
                        actionMessage = "Removed from favorites"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(actionMessage = "Failed to remove favorite")
            }
        }
    }

    /** Returns true if the hotel is currently in the favorites list */
    fun isFavorite(hotelId: String): Boolean = _state.value.favorites.any { it.id == hotelId }

    fun clearActionMessage() {
        _state.value = _state.value.copy(actionMessage = null)
    }
}
