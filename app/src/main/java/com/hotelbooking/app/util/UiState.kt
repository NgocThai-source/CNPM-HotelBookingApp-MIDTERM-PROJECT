package com.hotelbooking.app.util

/**
 * Generic sealed class representing UI state for any data type.
 * Used consistently across all ViewModels for state management.
 */
sealed class UiState<out T> {
    /** Initial idle state before any action */
    data object Idle : UiState<Nothing>()

    /** Loading state while fetching data */
    data object Loading : UiState<Nothing>()

    /** Success state with data */
    data class Success<T>(val data: T) : UiState<T>()

    /** Error state with message */
    data class Error(val message: String) : UiState<Nothing>()
}
