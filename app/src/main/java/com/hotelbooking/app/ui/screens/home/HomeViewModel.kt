package com.hotelbooking.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.data.repository.HotelListResponse
import com.hotelbooking.app.service.RetrofitClient
import com.hotelbooking.app.service.SSEService
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // True when refresh was triggered by a real-time SSE event (shows a subtle snackbar)
    private val _realtimeUpdated = MutableStateFlow(false)
    val realtimeUpdated: StateFlow<Boolean> = _realtimeUpdated

    // Debug raw JSON response
    private val _rawResponse = MutableStateFlow<String?>(null)
    val rawResponse: StateFlow<String?> = _rawResponse

    init {
        Log.d("HomeViewModel", ">>> INIT: HomeViewModel created, calling fetchHotels()")
        fetchHotels()
        connectSSE()
    }

    private fun connectSSE() {
        SSEService.events
            .onEach { event ->
                Log.d("HomeViewModel", ">>> SSE EVENT received: ${event.event}")
                when (event.event) {
                    "hotel_created", "hotel_updated", "hotel_deleted", "connected" -> {
                        _realtimeUpdated.value = true
                        fetchHotels()
                    }
                }
            }
            .launchIn(viewModelScope)

        SSEService.connect(viewModelScope)
    }

    fun fetchHotels() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d("HomeViewModel", ">>> FETCH: Starting API call to getAllHotels()...")

                val response = RetrofitClient.hotelApi.getAllHotels()

                Log.d("HomeViewModel", ">>> RESPONSE: response=$response, data class=${response?.data?.javaClass?.simpleName}, data size=${response?.data?.size ?: -1}")

                if (response != null && response.success) {
                    val hotelList = response.data ?: emptyList()
                    _hotels.value = hotelList
                } else {
                    Log.e("HomeViewModel", ">>> API ERROR: success=false, message=$response")
                    _error.value = "API returned failure${if (response == null) " (null response)" else ""}"
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", ">>> EXCEPTION: ${e::class.java.simpleName}: ${e.message}")
                e.printStackTrace()

                val errorMsg = when (e) {
                    is JsonSyntaxException -> "JSON parse error: ${e.message}"
                    is java.lang.IllegalStateException -> "JSON structure error: ${e.message}"
                    else -> e.message ?: "Unknown network error"
                }
                Log.e("HomeViewModel", ">>> Error type: ${e::class.java.name}")
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d("HomeViewModel", ">>> DONE: isLoading=false, hotels count=${_hotels.value.size}")
            }
        }
    }

    /** Called by UI to consume the realtime-updated flag after showing the snackbar */
    fun onRealtimeSnackbarShown() {
        _realtimeUpdated.value = false
    }

    override fun onCleared() {
        super.onCleared()
        SSEService.disconnect()
    }
}
