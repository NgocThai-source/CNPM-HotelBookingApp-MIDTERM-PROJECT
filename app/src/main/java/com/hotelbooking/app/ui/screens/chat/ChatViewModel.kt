package com.hotelbooking.app.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelbooking.app.data.model.Chat
import com.hotelbooking.app.data.model.Message
import com.hotelbooking.app.data.repository.AuthRepository
import com.hotelbooking.app.data.repository.ChatRepository
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
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val chatId: String = savedStateHandle[Constants.ARG_CHAT_ID] ?: ""

    val currentUserId: String get() = authRepository.currentUser?.uid ?: ""

    private val _chatsState = MutableStateFlow<UiState<List<Chat>>>(UiState.Loading)
    val chatsState: StateFlow<UiState<List<Chat>>> = _chatsState.asStateFlow()

    private val _messagesState = MutableStateFlow<UiState<List<Message>>>(UiState.Loading)
    val messagesState: StateFlow<UiState<List<Message>>> = _messagesState.asStateFlow()

    init {
        if (chatId.isNotBlank()) {
            loadMessages()
        }
        loadChats()
    }

    private fun loadChats() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            chatRepository.getUserChatsFlow(userId)
                .catch { e -> _chatsState.value = UiState.Error(e.message ?: "Lỗi") }
                .collect { _chatsState.value = UiState.Success(it) }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getChatMessagesFlow(chatId)
                .catch { e -> _messagesState.value = UiState.Error(e.message ?: "Lỗi") }
                .collect { _messagesState.value = UiState.Success(it) }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || chatId.isBlank()) return
        val userId = authRepository.currentUser?.uid ?: return

        viewModelScope.launch {
            val message = Message(senderId = userId, text = text)
            chatRepository.sendMessage(chatId, message)
        }
    }
}
