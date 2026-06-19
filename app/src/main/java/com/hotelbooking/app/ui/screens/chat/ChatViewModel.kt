package com.hotelbooking.app.ui.screens.chat

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.hotelbooking.app.data.model.ChatMessage
import com.hotelbooking.app.data.model.Conversation
import com.hotelbooking.app.data.model.ChatMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentConversation = MutableStateFlow<Conversation?>(null)
    val currentConversation: StateFlow<Conversation?> = _currentConversation

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText

    init {
        loadConversations()
    }

    fun loadConversations() {
        _isLoading.value = true
        _conversations.value = ChatMockData.conversations
        _isLoading.value = false
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun selectConversation(conversation: Conversation) {
        _currentConversation.value = conversation
        loadMessages(conversation.id)
    }

    fun loadMessages(conversationId: String) {
        _isLoading.value = true
        _messages.value = ChatMockData.getMessagesForConversation(conversationId)
        _isLoading.value = false
    }

    fun onMessageTextChange(text: String) {
        _messageText.value = text
    }

    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isEmpty()) return

        val convId = _currentConversation.value?.id ?: return
        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = convId,
            senderId = "me",
            text = text,
            timestamp = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date()),
            isMe = true
        )

        _messages.value = _messages.value + newMessage
        _messageText.value = ""

        val updatedConversations = _conversations.value.map { conv ->
            if (conv.id == convId) {
                conv.copy(lastMessage = text, timestamp = "Bây giờ", unreadCount = 0)
            } else conv
        }
        _conversations.value = updatedConversations
    }

    fun getFilteredConversations(): List<Conversation> {
        val query = _searchQuery.value.lowercase()
        return if (query.isEmpty()) {
            _conversations.value
        } else {
            _conversations.value.filter {
                it.participantName.lowercase().contains(query) ||
                    it.lastMessage.lowercase().contains(query)
            }
        }
    }

    fun clearCurrentConversation() {
        _currentConversation.value = null
        _messages.value = emptyList()
    }
}
