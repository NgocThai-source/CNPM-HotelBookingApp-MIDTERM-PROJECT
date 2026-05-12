package com.hotelbooking.app.ui.screens.auth.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// Design tokens synchronized with HomeScreen (CyanMain theme)
// ============================================================
object AuthColors {
    val CyanMain = Color(0xFF00E5FF)
    val CyanLight = Color(0xFFE0F7FA)
    val CyanDark = Color(0xFF00B8D4)

    // Dark mode palette
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkCardBorder = Color(0xFF2C2C2C)

    // Light mode palette
    val LightBackground = Color(0xFFF8F9FA)
    val LightSurface = Color.White

    // Text colors
    val TextPrimary = Color(0xFF1A1A2E)
    val TextSecondary = Color(0xFF6B7280)
    val TextOnDark = Color.White
    val TextOnDarkSecondary = Color(0xFFB0B0B0)

    fun background(isDarkMode: Boolean) = if (isDarkMode) DarkBackground else LightBackground
    fun surface(isDarkMode: Boolean) = if (isDarkMode) DarkSurface else LightSurface
    fun textPrimary(isDarkMode: Boolean) = if (isDarkMode) TextOnDark else TextPrimary
    fun textSecondary(isDarkMode: Boolean) = if (isDarkMode) TextOnDarkSecondary else TextSecondary
}

// ============================================================
// Auth Screen Scaffold – consistent layout for all auth pages
// ============================================================
@Composable
fun AuthScreenScaffold(
    isDarkMode: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val bgColor = AuthColors.background(isDarkMode)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = AuthColors.surface(isDarkMode)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

// ============================================================
// Scrollable Auth Screen Scaffold – for screens with more fields
// ============================================================
@Composable
fun ScrollableAuthScreenScaffold(
    isDarkMode: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val bgColor = AuthColors.background(isDarkMode)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = AuthColors.surface(isDarkMode)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

// ============================================================
// Auth Header – icon + title + subtitle
// ============================================================
@Composable
fun AuthHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDarkMode: Boolean
) {
    // Icon container with gradient background
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(AuthColors.CyanMain, AuthColors.CyanDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Color.White
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = AuthColors.textPrimary(isDarkMode)
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = subtitle,
        fontSize = 14.sp,
        color = AuthColors.textSecondary(isDarkMode)
    )
}

// ============================================================
// Styled OutlinedTextField for auth forms
// ============================================================
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = label,
                tint = AuthColors.CyanMain
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AuthColors.CyanMain,
            unfocusedBorderColor = if (isDarkMode) Color(0xFF3A3A3A) else Color(0xFFE0E0E0),
            focusedContainerColor = if (isDarkMode) Color(0xFF252525) else Color(0xFFF5F9FF),
            unfocusedContainerColor = if (isDarkMode) Color(0xFF252525) else Color(0xFFF5F9FF),
            focusedTextColor = AuthColors.textPrimary(isDarkMode),
            unfocusedTextColor = AuthColors.textPrimary(isDarkMode),
            focusedLabelColor = AuthColors.CyanMain,
            unfocusedLabelColor = AuthColors.textSecondary(isDarkMode),
            cursorColor = AuthColors.CyanMain,
            errorBorderColor = Color(0xFFEF5350),
            disabledBorderColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFE0E0E0),
            disabledContainerColor = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF0F0F0),
            disabledTextColor = AuthColors.textSecondary(isDarkMode),
            disabledLabelColor = AuthColors.textSecondary(isDarkMode)
        )
    )
}

// ============================================================
// Primary action button for auth forms
// ============================================================
@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthColors.CyanMain,
            disabledContainerColor = AuthColors.CyanMain.copy(alpha = 0.4f)
        ),
        enabled = enabled && !isLoading,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ============================================================
// Footer link row (e.g. "Don't have an account? Sign Up")
// ============================================================
@Composable
fun AuthFooterLink(
    normalText: String,
    linkText: String,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = normalText,
            color = AuthColors.textSecondary(isDarkMode),
            fontSize = 14.sp
        )
        TextButton(onClick = onClick) {
            Text(
                text = linkText,
                color = AuthColors.CyanMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
