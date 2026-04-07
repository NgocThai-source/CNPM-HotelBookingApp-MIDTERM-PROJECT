package com.hotelbooking.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Represents a chat conversation between a customer and a hotel manager.
 */
data class Chat(
    @DocumentId
    val id: String = "",
    val participants: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val lastMessage: String = "",
    val lastMessageAt: Timestamp? = null
)

/**
 * Represents a single message in a chat conversation.
 * Stored as a subcollection under messages/{chatId}/messages.
 */
data class Message(
    @DocumentId
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val isRead: Boolean = false,
    @ServerTimestamp
    val timestamp: Timestamp? = null
)
