package com.hotelbooking.app.data.model

/**
 * Data models for Chat management.
 * These are used for backend communication in ChatScreen.
 */

data class ChatListResponse(
    val success: Boolean,
    val message: String,
    val data: List<ChatConversation>? = null
)

data class ChatConversation(
    val chatId: String,
    val participantName: String,
    val participantAvatarUrl: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val hotelRelated: String? = null // Optional hotel name related to the chat
)

data class MessageHistoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<ChatMessage>? = null
)

data class ChatMessage(
    val messageId: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isMe: Boolean,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

data class SendMessageRequest(
    val chatId: String,
    val text: String
)

data class SendMessageResponse(
    val success: Boolean,
    val message: String,
    val data: ChatMessage? = null
)
