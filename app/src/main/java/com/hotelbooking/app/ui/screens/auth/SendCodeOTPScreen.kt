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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hotelbooking.app.R
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.screens.auth.components.*
import com.hotelbooking.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape as RS

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

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isTimerRunning = false
        }
    }

    LuxuryAuthScaffold(
        isDarkMode = isDarkMode,
        backgroundRes = R.drawable.auth_background,
        heroContent = {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Hotel Booking App",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verify Email",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Check your inbox for the recovery code",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    ) {
        Text(
            text = "Enter Verification Code",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary(isDarkMode),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        )

        // Sent-to hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Code sent to  ",
                fontSize = 14.sp,
                color = AppColors.textSecondary(isDarkMode)
            )
            Text(
                text = viewModel.email,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.SkyBrand
            )
        }

        // OTP input
        OtpInputField(
            otpValue = viewModel.otp,
            onOtpChange = { if (it.length <= 6) viewModel.otp = it },
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(32.dp))

        LuxuryPrimaryButton(
            text = "Verify Code",
            onClick = {
                if (viewModel.otp.length == 6) {
                    viewModel.verifyOTP { isSuccess ->
                        if (isSuccess) {
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

        // Resend + back row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    text = "Go Back",
                    color = AppColors.textSecondary(isDarkMode),
                    fontSize = 14.sp
                )
            }

            TextButton(
                onClick = {
                    viewModel.forgotPassword { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Code resent!", Toast.LENGTH_SHORT).show()
                            timeLeft = 60
                            isTimerRunning = true
                            viewModel.resetState()
                        }
                    }
                },
                enabled = !isTimerRunning && authState !is AuthState.Loading
            ) {
                Text(
                    text = if (isTimerRunning) "Resend (${timeLeft}s)" else "Resend Code",
                    color = if (isTimerRunning) AppColors.textTertiary(isDarkMode) else AppColors.SkyBrand,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ============================================================
// OTP Input — 6 individual digit boxes with gold focus
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
                    val isFilled = char != null

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isFocused -> AppColors.SkyBrand.copy(alpha = 0.06f)
                                    isFilled -> AppColors.SkyBrand.copy(alpha = 0.04f)
                                    else -> if (isDarkMode) AppColors.DarkElevated else Color(0xFFFBF9F6)
                                }
                            )
                            .border(
                                width = if (isFocused || isFilled) 2.dp else 1.5.dp,
                                color = when {
                                    isFocused -> AppColors.SkyBrand
                                    isFilled -> AppColors.SkyBrand.copy(alpha = 0.6f)
                                    else -> if (isDarkMode) AppColors.DarkBorder else AppColors.WarmBorder
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
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(22.dp)
                                    .background(AppColors.SkyBrand, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }
        }
    )
}
