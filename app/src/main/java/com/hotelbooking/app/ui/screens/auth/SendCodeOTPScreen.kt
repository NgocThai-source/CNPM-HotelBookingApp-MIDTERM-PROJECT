package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.screens.auth.components.*
import kotlinx.coroutines.delay

@Composable
fun SendCodeOTPScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val authState = viewModel.authState

    var timeLeft by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Listen for error state to show Toast
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    // Countdown timer
    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isTimerRunning = false
        }
    }

    AuthScreenScaffold(isDarkMode = isDarkMode) {
        // Header
        AuthHeader(
            icon = Icons.Filled.MarkEmailRead,
            title = "Verify Email",
            subtitle = "Enter the 6-digit code sent to:",
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Display email
        Text(
            text = viewModel.email,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AuthColors.CyanMain
        )

        Spacer(modifier = Modifier.height(28.dp))

        // OTP Input – individual digit boxes
        OtpInputField(
            otpValue = viewModel.otp,
            onOtpChange = { if (it.length <= 6) viewModel.otp = it },
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Verify button
        AuthPrimaryButton(
            text = "Verify OTP",
            onClick = {
                if (viewModel.otp.length == 6) {
                    viewModel.verifyOTP { isSuccess ->
                        if (isSuccess) {
                            // Reset state before navigating to prevent stale Success state
                            viewModel.resetState()
                            navController.navigate(Routes.RESET_PASSWORD)
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter all 6 digits", Toast.LENGTH_SHORT).show()
                }
            },
            isLoading = authState is AuthState.Loading,
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resend OTP
        TextButton(
            onClick = {
                viewModel.forgotPassword { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(context, "OTP code resent!", Toast.LENGTH_SHORT).show()
                        timeLeft = 60
                        isTimerRunning = true
                        viewModel.resetState()
                    }
                }
            },
            enabled = !isTimerRunning && authState !is AuthState.Loading
        ) {
            Text(
                text = if (isTimerRunning) "Resend code (${timeLeft}s)" else "Resend code",
                color = if (isTimerRunning) AuthColors.textSecondary(isDarkMode) else AuthColors.CyanMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Back button
        TextButton(onClick = { navController.popBackStack() }) {
            Text(
                "Go Back",
                color = AuthColors.textSecondary(isDarkMode),
                fontSize = 14.sp
            )
        }
    }
}

// ============================================================
// Custom OTP Input with individual digit boxes
// ============================================================
@Composable
private fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    isDarkMode: Boolean
) {
    BasicTextField(
        value = otpValue,
        onValueChange = { newValue ->
            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                onOtpChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                repeat(6) { index ->
                    val char = otpValue.getOrNull(index)
                    val isFocused = otpValue.length == index

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isDarkMode) Color(0xFF252525) else Color(0xFFF5F9FF)
                            )
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = when {
                                    isFocused -> AuthColors.CyanMain
                                    char != null -> AuthColors.CyanMain.copy(alpha = 0.5f)
                                    else -> if (isDarkMode) Color(0xFF3A3A3A) else Color(0xFFE0E0E0)
                                },
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char?.toString() ?: "",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuthColors.textPrimary(isDarkMode),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    )
}
