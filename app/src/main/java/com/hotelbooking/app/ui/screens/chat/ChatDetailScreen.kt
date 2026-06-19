package com.hotelbooking.app.ui.screens.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelbooking.app.data.model.ChatMessage
import com.hotelbooking.app.data.model.Conversation
import com.hotelbooking.app.ui.components.AppBottomNavBar
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

private val WarmSurface = AppColors.CreamSurface

@Composable
fun ChatDetailScreen(
    conversationId: String,
    participantName: String,
    isDarkMode: Boolean = false,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val currentConversation by viewModel.currentConversation.collectAsState()

    val surfaceColor = AppColors.background(dark = isDarkMode)
    val textPrimary = AppColors.textPrimary(dark = isDarkMode)
    val textSecondary = AppColors.textSecondary(dark = isDarkMode)
    val textTertiary = AppColors.textTertiary(dark = isDarkMode)

    // Warm Cream background — consistent with Search/Bookings/Alerts/Settings screens
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        val conv = viewModel.conversations.value.find { it.id == conversationId }
        if (conv != null) {
            viewModel.selectConversation(conv)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier,
        containerColor = Color.Transparent,
        bottomBar = {
            AppBottomNavBar(
                isDarkMode = isDarkMode,
                currentRoute = Routes.CHAT_DETAIL,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ChatDetailHeader(
                conversation = currentConversation,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                textTertiary = textTertiary,
                onBack = {
                    viewModel.clearCurrentConversation()
                    onBack()
                },
                fallbackName = participantName
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(if (isDarkMode) AppColors.DarkCard else WarmSurface)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        MessageBubbleItem(
                            message = message,
                            isDarkMode = isDarkMode,
                            textPrimary = textPrimary,
                            textTertiary = textTertiary
                        )
                    }
                }
            }

            ChatInputBar(
                messageText = messageText,
                onMessageTextChange = viewModel::onMessageTextChange,
                onSendMessage = viewModel::sendMessage,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textTertiary = textTertiary
            )
        }
    }
}

@Composable
private fun ChatDetailHeader(
    conversation: Conversation?,
    isDarkMode: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color,
    onBack: () -> Unit,
    fallbackName: String = "User"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDarkMode) AppColors.DarkSurface else AppColors.LightSurface
            )
    ) {
        Column {
            Spacer(modifier = Modifier.statusBarsPadding())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Avatar
                val avatarColors = conversation?.avatarColors
                    ?: listOf(AppColors.SkyBrand, AppColors.SkyLight)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(avatarColors)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (conversation?.participantName ?: fallbackName)
                            .split(" ")
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .joinToString(""),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = conversation?.participantName ?: fallbackName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (conversation?.isOnline == true) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Success)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Đang hoạt động",
                                fontSize = 12.sp,
                                color = textTertiary
                            )
                        } else {
                            Text(
                                text = "Offline",
                                fontSize = 12.sp,
                                color = textTertiary
                            )
                        }
                    }
                }

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Call",
                        tint = AppColors.SkyBrand,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Menu",
                        tint = textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            HorizontalDivider(
                color = if (isDarkMode) AppColors.DarkBorder else AppColors.LightBorder,
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
private fun MessageBubbleItem(
    message: ChatMessage,
    isDarkMode: Boolean,
    textPrimary: Color,
    textTertiary: Color
) {
    val isMe = message.isMe
    val bubbleBg = if (isMe) {
        Brush.horizontalGradient(listOf(AppColors.SkyBrand, AppColors.NavyMid))
    } else {
        Brush.horizontalGradient(
            listOf(
                if (isDarkMode) AppColors.DarkCard else AppColors.CreamSurface,
                if (isDarkMode) AppColors.DarkCard else AppColors.CreamLight
            )
        )
    }

    val textColor = if (isMe) Color.White else textPrimary
    val alignment = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.75f).dp)
                .clip(
                    RoundedCornerShape(
                        topStart = if (isMe) 18.dp else 4.dp,
                        topEnd = if (isMe) 4.dp else 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    )
                )
                .background(bubbleBg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                color = textColor,
                lineHeight = 20.sp
            )
        }

        Text(
            text = message.timestamp,
            fontSize = 11.sp,
            color = textTertiary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ChatInputBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isDarkMode: Boolean,
    textPrimary: Color,
    textTertiary: Color
) {
    val surfaceColor = if (isDarkMode) AppColors.DarkSurface else WarmSurface
    val inputBgColor = if (isDarkMode) AppColors.DarkCard else AppColors.CreamLight

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor)
    ) {
        Column {
            HorizontalDivider(
                color = if (isDarkMode) AppColors.DarkBorder else AppColors.LightBorder,
                thickness = 0.5.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attachment button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(inputBgColor)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Attach",
                        tint = AppColors.SkyBrand,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(inputBgColor)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = messageText,
                        onValueChange = onMessageTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 4,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = textPrimary
                        ),
                        decorationBox = { inner ->
                            Box {
                                if (messageText.isEmpty()) {
                                    Text(
                                        text = "Type a message...",
                                        fontSize = 14.sp,
                                        color = textTertiary
                                    )
                                }
                                inner()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Send button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (messageText.isNotBlank()) {
                                Brush.horizontalGradient(
                                    listOf(AppColors.SkyBrand, AppColors.SkyLight)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    listOf(
                                        inputBgColor,
                                        inputBgColor
                                    )
                                )
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    onSendMessage()
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (messageText.isNotBlank()) Color.White else textTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
