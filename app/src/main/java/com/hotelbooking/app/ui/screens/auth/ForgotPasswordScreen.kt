package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelbooking.app.R
import com.hotelbooking.app.ui.screens.auth.components.*
import com.hotelbooking.app.ui.theme.AppColors

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit,
    onNavigateToOTP: () -> Unit,
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val authState = viewModel.authState
    var email by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
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
                text = "Forgot Password?",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We'll send a recovery code to your email",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    ) {
        Text(
            text = "Account Recovery",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary(isDarkMode),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Text(
            text = "Enter the email address linked to your account",
            fontSize = 14.sp,
            color = AppColors.textSecondary(isDarkMode),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        LuxuryTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email address",
            leadingIcon = Icons.Filled.Email,
            isDarkMode = isDarkMode,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(28.dp))

        LuxuryPrimaryButton(
            text = "Send Recovery Code",
            onClick = {
                if (email.isNotBlank()) {
                    viewModel.email = email
                    viewModel.forgotPassword { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Recovery code sent!", Toast.LENGTH_SHORT).show()
                            onNavigateToOTP()
                            viewModel.resetState()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                }
            },
            isLoading = authState is AuthState.Loading,
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Back to Sign In",
                color = AppColors.SkyBrand,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
