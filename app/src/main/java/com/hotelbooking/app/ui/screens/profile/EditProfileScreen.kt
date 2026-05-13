package com.hotelbooking.app.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current

    // Bảng màu yêu cầu
    val primaryColor = Color(0xFF00E5FF)
    val accentColor = Color(0xFFFFC107)

    var name by remember { mutableStateOf("Alex Nguyen") }
    var phone by remember { mutableStateOf("0901234567") }
    val email = "vannguyen11a21@gmail.com"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Personal Information", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. AVATAR WITH PRIMARY COLOR BORDER ---
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    border = androidx.compose.foundation.BorderStroke(2.dp, primaryColor)
                ) {
                    Icon(Icons.Outlined.Person, null, modifier = Modifier.padding(20.dp), tint = primaryColor.copy(alpha = 0.4f))
                }
                // Camera button with Accent Color (Vàng)
                Surface(
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    color = accentColor,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.padding(8.dp).size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. INPUT FIELDS ---
            ModernInputField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                icon = Icons.Outlined.Person,
                enabled = true,
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            ModernInputField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it },
                label = "Phone Number",
                icon = Icons.Outlined.PhoneIphone,
                enabled = true,
                keyboardType = KeyboardType.Number,
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            ModernInputField(
                value = email,
                onValueChange = {},
                label = "Email Address (Read-only)",
                icon = Icons.Outlined.Email,
                enabled = false,
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // --- 3. SAVE BUTTON (PRIMARY COLOR) ---
            Button(
                onClick = {
                    if (phone.length != 10) {
                        Toast.makeText(context, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show()
                    } else if (name.isBlank()) {
                        Toast.makeText(context, "Full name cannot be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    primaryColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) primaryColor else Color.LightGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (enabled) primaryColor else Color.LightGray
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color(0xFFEEEEEE),
                disabledBorderColor = Color(0xFFF5F5F5),
                containerColor = if (enabled) primaryColor.copy(alpha = 0.03f) else Color(0xFFF5F5F5),
                disabledTextColor = Color.Gray
            )
        )
    }
}