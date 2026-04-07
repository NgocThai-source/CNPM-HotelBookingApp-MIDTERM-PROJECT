package com.hotelbooking.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.data.repository.HotelRepository
import com.hotelbooking.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen.
 * Manages hotel list, search, and filtering.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _hotelsState = MutableStateFlow<UiState<List<Hotel>>>(UiState.Loading)
    val hotelsState: StateFlow<UiState<List<Hotel>>> = _hotelsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Hotel>>(emptyList())
    val searchResults: StateFlow<List<Hotel>> = _searchResults.asStateFlow()

    init {
        loadHotels()
    }

    /**
     * Load all hotels from Firestore (real-time).
     */
    private fun loadHotels() {
        viewModelScope.launch {
            hotelRepository.getHotelsFlow()
                .catch { e ->
                    _hotelsState.value = UiState.Error(e.message ?: "Lỗi tải danh sách khách sạn")
                }
                .collect { hotels ->
                    _hotelsState.value = UiState.Success(hotels)
                }
        }
    }

    /**
     * Search hotels by city or name.
     */
    fun search(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            hotelRepository.searchHotels(query)
                .onSuccess { _searchResults.value = it }
                .onFailure { _searchResults.value = emptyList() }
        }
    }
}
