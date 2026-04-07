package com.hotelbooking.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hotelbooking.app.data.model.Chat
import com.hotelbooking.app.data.model.Message
import com.hotelbooking.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for chat operations.
 * Handles real-time messaging between customers and hotel managers using Firestore.
 */
@Singleton
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    /**
     * Get all chat conversations for a user, real-time.
     */
    fun getUserChatsFlow(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSAGES)
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val chats = snapshot?.toObjects(Chat::class.java) ?: emptyList()
                trySend(chats)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Get messages for a specific chat, real-time, ordered chronologically.
     */
    fun getChatMessagesFlow(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSAGES)
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Send a message in a chat.
     * Also updates the chat's last message and timestamp.
     */
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
        return try {
            val batch = firestore.batch()

            // Add message to subcollection
            val messageRef = firestore.collection(Constants.COLLECTION_MESSAGES)
                .document(chatId)
                .collection("messages")
                .document()
            batch.set(messageRef, message)

            // Update chat's last message
            val chatRef = firestore.collection(Constants.COLLECTION_MESSAGES)
                .document(chatId)
            batch.update(chatRef, mapOf(
                "lastMessage" to message.text,
                "lastMessageAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            ))

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create or get an existing chat between two users.
     */
    suspend fun getOrCreateChat(
        userId: String,
        userName: String,
        otherUserId: String,
        otherUserName: String
    ): Result<String> {
        return try {
            // Check if chat already exists
            val existingChat = firestore.collection(Constants.COLLECTION_MESSAGES)
                .whereArrayContains("participants", userId)
                .get()
                .await()

            val existing = existingChat.toObjects(Chat::class.java)
                .find { it.participants.contains(otherUserId) }

            if (existing != null) {
                return Result.success(existing.id)
            }

            // Create new chat
            val chat = Chat(
                participants = listOf(userId, otherUserId),
                participantNames = mapOf(userId to userName, otherUserId to otherUserName),
                lastMessage = "",
                lastMessageAt = null
            )

            val docRef = firestore.collection(Constants.COLLECTION_MESSAGES)
                .add(chat)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark all messages in a chat as read for a specific user.
     */
    suspend fun markMessagesAsRead(chatId: String, userId: String): Result<Unit> {
        return try {
            val unreadMessages = firestore.collection(Constants.COLLECTION_MESSAGES)
                .document(chatId)
                .collection("messages")
                .whereNotEqualTo("senderId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()
            unreadMessages.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
