package com.hotelbooking.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nguyễn Văn A", style = MaterialTheme.typography.headlineMedium)
        Text("Thành viên VIP", color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = { /* Mở Manager Dashboard */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Quản lý Khách sạn (Manager)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Đăng xuất", color = MaterialTheme.colorScheme.error)
        }
    }
}