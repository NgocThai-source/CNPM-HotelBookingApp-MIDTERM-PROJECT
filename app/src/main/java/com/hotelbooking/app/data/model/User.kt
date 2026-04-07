package com.hotelbooking.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Represents a user in the system.
 * Can be either a Customer or a Hotel Manager.
 */
data class User(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

/**
 * User roles in the application.
 */
enum class UserRole {
    CUSTOMER,
    MANAGER;

    companion object {
        fun fromString(value: String): UserRole {
            return when (value.lowercase()) {
                "manager" -> MANAGER
                else -> CUSTOMER
            }
        }
    }
}
