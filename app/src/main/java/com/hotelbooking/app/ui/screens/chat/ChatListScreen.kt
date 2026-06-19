package com.hotelbooking.app.ui.screens.chat

import androidx.compose.animation.*
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hotelbooking.app.data.model.Conversation
import com.hotelbooking.app.ui.components.AppBottomNavBar
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors

@Composable
fun ChatListScreen(
    isDarkMode: Boolean = false,
    onNavigateToDetail: (String, String) -> Unit = { _, _ -> },
    onNavigate: (String) -> Unit = {}
) {
    val viewModel: ChatViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredConversations by remember(searchQuery) {
        mutableStateOf(viewModel.getFilteredConversations())
    }
    val isLoading by viewModel.isLoading.collectAsState()

    val surfaceColor = AppColors.background(dark = isDarkMode)
    val cardColor = AppColors.card(dark = isDarkMode)
    val textPrimary = AppColors.textPrimary(dark = isDarkMode)
    val textSecondary = AppColors.textSecondary(dark = isDarkMode)
    val textTertiary = AppColors.textTertiary(dark = isDarkMode)

    // Warm Cream background — consistent with Search/Bookings/Alerts/Settings screens
    val WarmSurface = AppColors.CreamSurface

    val listState = rememberLazyListState()

    LaunchedEffect(searchQuery) {
        // Trigger recomposition when search changes
    }

    Scaffold(
        modifier = Modifier,
        containerColor = Color.Transparent,
        bottomBar = {
            AppBottomNavBar(
                isDarkMode = isDarkMode,
                currentRoute = Routes.CHAT_LIST,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            MessagesHeader(
                isDarkMode = isDarkMode,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                surfaceColor = if (isDarkMode) AppColors.DarkCard else WarmSurface
            )

            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(if (isDarkMode) AppColors.DarkCard else WarmSurface)
            ) {
                if (isLoading) {
                    SkeletonConversationList(isDarkMode = isDarkMode)
                } else if (filteredConversations.isEmpty()) {
                    EmptyConversationsState(
                        isDarkMode = isDarkMode,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        surfaceColor = if (isDarkMode) AppColors.DarkCard else WarmSurface
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = filteredConversations,
                            key = { it.id }
                        ) { conversation ->
                            ConversationCard(
                                conversation = conversation,
                                isDarkMode = isDarkMode,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                textTertiary = textTertiary,
                                cardColor = cardColor,
                                surfaceColor = if (isDarkMode) AppColors.DarkCard else WarmSurface,
                                onClick = {
                                    viewModel.selectConversation(conversation)
                                    onNavigateToDetail(conversation.id, conversation.participantName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessagesHeader(
    isDarkMode: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    surfaceColor: Color
) {
    val headerHeight = 200.dp
    val gradientHeight = 160.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        // Navy gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(gradientHeight)
                .background(
                    Brush.verticalGradient(
                        listOf(AppColors.NavyDeep, AppColors.NavyMid)
                    )
                )
        )

        // Radial glow top-right
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        listOf(
                            AppColors.SkyBrand.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Messages",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Tin nhắn",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Kết nối và trò chuyện",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.65f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Search bar
            MessagesSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                isDarkMode = isDarkMode
            )
        }

        // Bottom fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, surfaceColor)
                    )
                )
        )
    }
}

@Composable
private fun MessagesSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isDarkMode: Boolean
) {
    val bgColor = if (isDarkMode) AppColors.DarkCard else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.15f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = AppColors.SkyBrand,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = AppColors.textPrimary(dark = isDarkMode)
                ),
                decorationBox = { inner ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search conversations...",
                                fontSize = 14.sp,
                                color = AppColors.textTertiary(dark = isDarkMode)
                            )
                        }
                        inner()
                    }
                }
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear",
                        tint = AppColors.textTertiary(dark = isDarkMode),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationCard(
    conversation: Conversation,
    isDarkMode: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color,
    cardColor: Color,
    surfaceColor: Color,
    onClick: () -> Unit
) {
    val dividerColor = if (isDarkMode) AppColors.DarkBorder else AppColors.WarmBorder
    val interactionSource = remember { MutableInteractionSource() }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = 50),
        label = "card_alpha"
    )
    val slide by animateFloatAsState(
        targetValue = if (visible) 0f else 30f,
        animationSpec = tween(400, delayMillis = 50),
        label = "card_slide"
    )

    Column(modifier = Modifier.graphicsLayer { 
        this.alpha = alpha 
        this.translationY = slide
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with gradient
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(conversation.avatarColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.participantName.split(" ")
                        .take(2)
                        .mapNotNull { it.firstOrNull()?.uppercase() }
                        .joinToString(""),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.participantName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = conversation.timestamp,
                        fontSize = 12.sp,
                        color = textTertiary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        fontSize = 13.sp,
                        color = textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(AppColors.SkyBrand),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (conversation.unreadCount > 9) "9+" else conversation.unreadCount.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(start = 80.dp),
            color = dividerColor,
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun EmptyConversationsState(
    isDarkMode: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    surfaceColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppColors.SkyBrand.copy(alpha = 0.15f),
                            AppColors.NavyMid.copy(alpha = 0.06f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ChatBubbleOutline,
                contentDescription = null,
                tint = AppColors.SkyBrand.copy(alpha = 0.7f),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Chưa có tin nhắn nào",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bắt đầu cuộc trò chuyện với khách sạn bạn quan tâm",
            fontSize = 14.sp,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun SkeletonConversationList(isDarkMode: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(4) {
            SkeletonConversationCard(isDarkMode = isDarkMode)
        }
    }
}

@Composable
private fun SkeletonConversationCard(isDarkMode: Boolean) {
    val shimmerColors = if (isDarkMode) {
        listOf(
            AppColors.DarkCard.copy(alpha = 0.6f),
            AppColors.DarkCard.copy(alpha = 0.2f),
            AppColors.DarkCard.copy(alpha = 0.6f)
        )
    } else {
        listOf(
            Color(0xFFE0E0E0).copy(alpha = 0.6f),
            Color(0xFFF5F5F5).copy(alpha = 0.6f),
            Color(0xFFE0E0E0).copy(alpha = 0.6f)
        )
    }

    val transitionState = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by transitionState.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_alpha"
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(shimmerColors[0].copy(alpha = shimmerAlpha))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .width(120.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerColors[0].copy(alpha = shimmerAlpha))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerColors[1].copy(alpha = shimmerAlpha))
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(shimmerColors[0].copy(alpha = shimmerAlpha))
            )
        }
    }
}
