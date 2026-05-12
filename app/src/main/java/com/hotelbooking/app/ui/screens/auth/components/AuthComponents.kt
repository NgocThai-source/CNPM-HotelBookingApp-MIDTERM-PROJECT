package com.hotelbooking.app.ui.screens.auth.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelbooking.app.R
import com.hotelbooking.app.ui.theme.AppColors

// ============================================================
// Design tokens – re-exported for backward compatibility
// ============================================================
object AuthColors {
    val CyanMain = AppColors.CyanMain
    val CyanLight = AppColors.CyanSurface
    val CyanDark = AppColors.CyanDark

    fun background(isDarkMode: Boolean) = AppColors.background(isDarkMode)
    fun surface(isDarkMode: Boolean) = AppColors.surface(isDarkMode)
    fun textPrimary(isDarkMode: Boolean) = AppColors.textPrimary(isDarkMode)
    fun textSecondary(isDarkMode: Boolean) = AppColors.textSecondary(isDarkMode)
}

// ============================================================
// Auth Screen Scaffold – premium card-centered layout
// ============================================================
@Composable
fun AuthScreenScaffold(
    isDarkMode: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "scaffold_fade"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(
                        colors = listOf(
                            AppColors.DarkBackground,
                            Color(0xFF0D1520),
                            AppColors.DarkBackground
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F7FA),
                            AppColors.LightBackground,
                            Color(0xFFE8EDF5)
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .alpha(alpha)
                .shadow(
                    elevation = if (isDarkMode) 0.dp else 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = AppColors.CyanMain.copy(alpha = 0.08f),
                    spotColor = AppColors.CyanMain.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

// ============================================================
// Scrollable Auth Screen Scaffold – for Register & long forms
// ============================================================
@Composable
fun ScrollableAuthScreenScaffold(
    isDarkMode: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "scroll_scaffold_fade"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(
                        colors = listOf(
                            AppColors.DarkBackground,
                            Color(0xFF0D1520),
                            AppColors.DarkBackground
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F7FA),
                            AppColors.LightBackground,
                            Color(0xFFE8EDF5)
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .alpha(alpha)
                .shadow(
                    elevation = if (isDarkMode) 0.dp else 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = AppColors.CyanMain.copy(alpha = 0.08f),
                    spotColor = AppColors.CyanMain.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 28.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

// ============================================================
// Auth Header – App logo + title + subtitle
// ============================================================
@Composable
fun AuthHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDarkMode: Boolean
) {
    // App logo from drawable resource
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "Hotel Booking App",
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = title,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = AppColors.textPrimary(isDarkMode),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = subtitle,
        fontSize = 14.sp,
        color = AppColors.textSecondary(isDarkMode),
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
    )
}

// ============================================================
// Styled OutlinedTextField – refined input styling
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
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = label,
                tint = if (isError) AppColors.Error else AppColors.CyanMain,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.CyanMain,
            unfocusedBorderColor = AppColors.border(isDarkMode),
            focusedContainerColor = if (isDarkMode) AppColors.DarkElevated else AppColors.CyanSubtle,
            unfocusedContainerColor = if (isDarkMode) AppColors.DarkSurface else Color(0xFFF8FAFB),
            focusedTextColor = AppColors.textPrimary(isDarkMode),
            unfocusedTextColor = AppColors.textPrimary(isDarkMode),
            focusedLabelColor = AppColors.CyanMain,
            unfocusedLabelColor = AppColors.textSecondary(isDarkMode),
            cursorColor = AppColors.CyanMain,
            errorBorderColor = AppColors.Error,
            errorContainerColor = AppColors.Error.copy(alpha = 0.05f),
            disabledBorderColor = AppColors.border(isDarkMode).copy(alpha = 0.5f),
            disabledContainerColor = if (isDarkMode) AppColors.DarkBackground else Color(0xFFF0F0F0),
            disabledTextColor = AppColors.textTertiary(isDarkMode),
            disabledLabelColor = AppColors.textTertiary(isDarkMode)
        )
    )
}

// ============================================================
// Primary action button – gradient-styled with loading state
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
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.CyanMain,
            disabledContainerColor = AppColors.CyanMain.copy(alpha = 0.35f)
        ),
        enabled = enabled && !isLoading,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = normalText,
            color = AppColors.textSecondary(isDarkMode),
            fontSize = 14.sp
        )
        TextButton(onClick = onClick) {
            Text(
                text = linkText,
                color = AppColors.CyanMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
