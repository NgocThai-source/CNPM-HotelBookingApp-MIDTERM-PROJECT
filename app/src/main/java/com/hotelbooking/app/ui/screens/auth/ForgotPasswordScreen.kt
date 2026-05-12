package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelbooking.app.ui.screens.auth.components.*

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

    // Listen for error state to show Toast
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    AuthScreenScaffold(isDarkMode = isDarkMode) {
        // Header
        AuthHeader(
            icon = Icons.Filled.LockReset,
            title = "Forgot Password?",
            subtitle = "Enter your email to receive a recovery code",
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Email field
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            leadingIcon = Icons.Filled.Email,
            isDarkMode = isDarkMode,
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Send Code button
        AuthPrimaryButton(
            text = "Send Recovery Code",
            onClick = {
                if (email.isNotBlank()) {
                    viewModel.email = email
                    viewModel.forgotPassword { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "OTP code has been sent!", Toast.LENGTH_SHORT).show()
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

        // Back to Login
        TextButton(onClick = onBackToLogin) {
            Text(
                "Back to Sign In",
                color = AuthColors.CyanMain,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
