package com.hotelbooking.app.data.model

import androidx.compose.ui.graphics.Color
import com.hotelbooking.app.ui.theme.AppColors

data class Conversation(
    val id: String,
    val participantName: String,
    val participantAvatar: String?,
    val avatarColors: List<Color>,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean = false
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isMe: Boolean
)

object ChatMockData {
    val conversations = listOf(
        Conversation(
            id = "conv_1",
            participantName = "Mai Nguyen",
            participantAvatar = null,
            avatarColors = listOf(AppColors.SkyBrand, AppColors.SkyLight),
            lastMessage = "Cảm ơn bạn! Phòng rất tuyệt vời!",
            timestamp = "2m",
            unreadCount = 2,
            isOnline = true
        ),
        Conversation(
            id = "conv_2",
            participantName = "Linh Ho",
            participantAvatar = null,
            avatarColors = listOf(AppColors.GoldPrimary, AppColors.GoldLightCustom),
            lastMessage = "Cần hỗ trợ gì thêm không bạn?",
            timestamp = "15m",
            unreadCount = 1,
            isOnline = false
        ),
        Conversation(
            id = "conv_3",
            participantName = "Nam Pham",
            participantAvatar = null,
            avatarColors = listOf(AppColors.SkyBrand, AppColors.GoldPrimary),
            lastMessage = "Đã xác nhận booking thành công",
            timestamp = "1h",
            unreadCount = 0,
            isOnline = true
        ),
        Conversation(
            id = "conv_4",
            participantName = "Ha Tran",
            participantAvatar = null,
            avatarColors = listOf(AppColors.NavyMid, AppColors.SkyBrand),
            lastMessage = "Check-in lúc 14:00 nhé",
            timestamp = "Hôm qua",
            unreadCount = 0,
            isOnline = false
        ),
        Conversation(
            id = "conv_5",
            participantName = "Khoa Le",
            participantAvatar = null,
            avatarColors = listOf(AppColors.GoldPrimary, AppColors.GoldDark),
            lastMessage = "Thanh toán đã được xử lý",
            timestamp = "2 ngày",
            unreadCount = 0,
            isOnline = false
        )
    )

    private val messagesForConv1 = listOf(
        ChatMessage("m1", "conv_1", "me", "Xin chào, tôi muốn hỏi về phòng Deluxe Ocean View", "10:30", true),
        ChatMessage("m2", "conv_1", "other", "Dạ xin chào! Phòng Deluxe Ocean View còn trống ạ. Giá là 2.5 triệu/đêm.", "10:32", false),
        ChatMessage("m3", "conv_1", "me", "Có bao gồm bữa sáng không?", "10:33", true),
        ChatMessage("m4", "conv_1", "other", "Dạ có ạ, buffet sáng miễn phí cho 2 người lớn mỗi ngày.", "10:35", false),
        ChatMessage("m5", "conv_1", "me", "Tuyệt vời! Tôi đặt 2 đêm nhé.", "10:36", true),
        ChatMessage("m6", "conv_1", "other", "Dạ perfect ạ! Mình sẽ giữ phòng cho bạn. Check-in lúc 14:00, check-out 12:00 ngày hôm sau.", "10:38", false),
        ChatMessage("m7", "conv_1", "me", "Cảm ơn bạn! Phòng rất tuyệt vời!", "10:40", true)
    )

    private val messagesForConv2 = listOf(
        ChatMessage("m10", "conv_2", "other", "Xin chào! Mình là Linh, hỗ trợ khách hàng của HotelBooking. Cần giúp gì không ạ?", "09:00", false),
        ChatMessage("m11", "conv_2", "me", "Mình muốn đổi ngày check-in", "09:15", true),
        ChatMessage("m12", "conv_2", "other", "Dạ không sao ạ. Ngày mới bạn muốn là ngày nào?", "09:16", false),
        ChatMessage("m13", "conv_2", "me", "Chuyển sang ngày 25 tháng 6 được không?", "09:18", true),
        ChatMessage("m14", "conv_2", "other", "Cần hỗ trợ gì thêm không bạn?", "09:20", false)
    )

    private val messagesForConv3 = listOf(
        ChatMessage("m20", "conv_3", "me", "Booking của mình đã được xử lý chưa?", "14:00", true),
        ChatMessage("m21", "conv_3", "other", "Dạ đang kiểm tra ngay ạ...", "14:02", false),
        ChatMessage("m22", "conv_3", "other", "Đã xác nhận booking thành công", "14:05", false)
    )

    fun getMessagesForConversation(conversationId: String): List<ChatMessage> {
        return when (conversationId) {
            "conv_1" -> messagesForConv1
            "conv_2" -> messagesForConv2
            "conv_3" -> messagesForConv3
            else -> emptyList()
        }
    }
}
