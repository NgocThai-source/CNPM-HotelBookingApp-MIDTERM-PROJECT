package com.hotelbooking.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.components.BottomNavBar
import com.hotelbooking.app.ui.components.BottomNavItem
import com.hotelbooking.app.ui.components.CyanMain
import com.hotelbooking.app.ui.components.CyanLight

// ── Mock Chat Data ──
data class ChatSummary(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val hotelName: String? = null
)

private val mockChats = listOf(
    ChatSummary("1", "Support Team", "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg", "Your booking at The Azure has been confirmed!", "10:30 AM", 2, true, "The Azure Grand Resort"),
    ChatSummary("2", "Host Tran", "https://images.pexels.com/photos/718978/pexels-photo-718978.jpeg", "Sure, I'll prepare the extra towels for you.", "Yesterday", 0, false, "Emerald Isle Resort"),
    ChatSummary("3", "Agoda Homes", "https://images.pexels.com/photos/1212984/pexels-photo-1212984.jpeg", "Please provide your ID for verification.", "Monday", 0, true, "Oasis Sands Resort"),
    ChatSummary("4", "Ms. Jessica", "https://images.pexels.com/photos/1181682/pexels-photo-1181682.jpeg", "The breakfast is served from 7 AM to 10 AM.", "Oct 10", 0, false, "The Ritz-Carlton Sky"),
    ChatSummary("5", "Aman Resorts", "https://images.pexels.com/photos/1587009/pexels-photo-1587009.jpeg", "Welcome to Amanpuri! How can we help?", "Oct 08", 0, true, "Amanpuri Hideaway"),
    ChatSummary("6", "Concierge Service", "https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg", "Your airport shuttle is scheduled for 2 PM.", "Oct 05", 1, true)
)

@Composable
fun ChatListScreen(onNavigate: (String) -> Unit = {}, onChatClick: (String) -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = BottomNavItem.CHAT.route,
                onItemClick = { item -> onNavigate(item.route) }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Top Bar ──
            ChatTopBar()

            // ── Search Section ──
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search messages...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = CyanMain,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // ── Online Contacts Row ──
            Text(
                "Active Now",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            OnlineContactsRow(mockChats.filter { it.isOnline })

            Spacer(modifier = Modifier.height(24.dp))

            // ── Chat List ──
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                LazyColumn(
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text(
                            "Recent Messages",
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    items(mockChats) { chat ->
                        ChatItem(chat = chat, onClick = { onChatClick(chat.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ChatTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Messages", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text("Keep in touch with your hosts", fontSize = 12.sp, color = Color.Gray)
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(Icons.Outlined.MoreVert, contentDescription = null, tint = Color.Black)
        }
    }
}

@Composable
fun OnlineContactsRow(contacts: List<ChatSummary>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(contacts) { contact ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    AsyncImage(
                        model = contact.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF4CAF50)))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    contact.name.split(" ").first(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ChatItem(chat: ChatSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box {
            AsyncImage(
                model = chat.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF4CAF50)))
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    chat.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(chat.time, fontSize = 11.sp, color = Color.Gray)
            }
            
            if (chat.hotelName != null) {
                Text(
                    chat.hotelName,
                    fontSize = 11.sp,
                    color = CyanMain,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Text(
                chat.lastMessage,
                fontSize = 13.sp,
                color = if (chat.unreadCount > 0) Color.Black else Color.Gray,
                fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (chat.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(CyanMain),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${chat.unreadCount}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
