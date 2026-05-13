package com.hotelbooking.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.components.CyanMain
import com.hotelbooking.app.ui.components.CyanLight

data class Message(
    val id: String,
    val text: String,
    val time: String,
    val isMe: Boolean
)

@Composable
fun ChatDetailScreen(chatId: String, onBack: () -> Unit = {}) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("1", "Hello! How can I help you today?", "10:00 AM", false),
            Message("2", "I'd like to check my booking confirmation.", "10:05 AM", true),
            Message("3", "Of course! Your booking at The Azure Grand Resort is confirmed for Oct 12-15.", "10:06 AM", false),
            Message("4", "Great, thank you! Is breakfast included?", "10:10 AM", true),
            Message("5", "Yes, breakfast is included in your stay.", "10:11 AM", false)
        )
    }

    Scaffold(
        topBar = {
            ChatDetailTopBar(onBack = onBack)
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }

            // ── Message Input ──
            MessageInput(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        messages.add(Message("${messages.size + 1}", messageText, "10:15 AM", true))
                        messageText = ""
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    AsyncImage(
                        model = "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg",
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.White).padding(1.5.dp).align(Alignment.BottomEnd)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF4CAF50)))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Support Team", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Online", fontSize = 12.sp, color = Color(0xFF4CAF50))
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Filled.Call, contentDescription = null, tint = CyanMain) }
            IconButton(onClick = {}) { Icon(Icons.Filled.Videocam, contentDescription = null, tint = CyanMain) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun MessageBubble(message: Message) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isMe) 16.dp else 0.dp,
                bottomEnd = if (message.isMe) 0.dp else 16.dp
            ),
            color = if (message.isMe) CyanMain else Color.White,
            tonalElevation = 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (message.isMe) Color.White else Color.Black,
                fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = message.time, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun MessageInput(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(CyanLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = CyanMain)
            }
            
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...", fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFF1F3F4),
                    focusedContainerColor = Color(0xFFF1F3F4)
                ),
                maxLines = 4
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyanMain)
                    .clickable { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}
