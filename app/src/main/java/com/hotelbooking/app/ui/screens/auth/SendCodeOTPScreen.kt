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
import com.hotelbooking.app.ui.theme.AppColors
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
        // Header with logo
        AuthHeader(
            icon = Icons.Filled.MarkEmailRead,
            title = "Verify Email",
            subtitle = "Enter the 6-digit code sent to:",
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Display email
        Text(
            text = viewModel.email,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.CyanMain
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OTP Input – individual digit boxes
        OtpInputField(
            otpValue = viewModel.otp,
            onOtpChange = { if (it.length <= 6) viewModel.otp = it },
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(32.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

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
                color = if (isTimerRunning) AppColors.textSecondary(isDarkMode) else AppColors.CyanMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Back button
        TextButton(onClick = { navController.popBackStack() }) {
            Text(
                "Go Back",
                color = AppColors.textSecondary(isDarkMode),
                fontSize = 14.sp
            )
        }
    }
}

// ============================================================
// Custom OTP Input with individual digit boxes – refined design
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
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val char = otpValue.getOrNull(index)
                    val isFocused = otpValue.length == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isDarkMode) AppColors.DarkElevated else AppColors.CyanSubtle
                            )
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = when {
                                    isFocused -> AppColors.CyanMain
                                    char != null -> AppColors.CyanMain.copy(alpha = 0.5f)
                                    else -> AppColors.border(isDarkMode)
                                },
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (char != null) {
                            Text(
                                text = char.toString(),
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.textPrimary(isDarkMode),
                                    textAlign = TextAlign.Center
                                )
                            )
                        } else if (isFocused) {
                            // Blinking cursor indicator
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(24.dp)
                                    .background(
                                        AppColors.CyanMain,
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    )
}
