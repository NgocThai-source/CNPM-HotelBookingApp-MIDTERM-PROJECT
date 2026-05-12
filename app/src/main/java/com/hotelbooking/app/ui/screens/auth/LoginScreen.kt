package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hotelbooking.app.data.model.LoginRequest
import com.hotelbooking.app.ui.screens.auth.components.*

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: AuthViewModel,
    isDarkMode: Boolean = false
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val authState = viewModel.authState

    // Listen for auth state changes to show Toast messages
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        } else if (authState is AuthState.Success) {
            Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    AuthScreenScaffold(isDarkMode = isDarkMode) {
        // Header
        AuthHeader(
            icon = Icons.Filled.HomeWork,
            title = "Welcome Back!",
            subtitle = "Sign in to book your next stay",
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Filled.Lock,
            isDarkMode = isDarkMode,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = authState !is AuthState.Loading,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = AuthColors.CyanMain
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Forgot password link
        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                "Forgot Password?",
                color = AuthColors.CyanMain,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign In button
        AuthPrimaryButton(
            text = "Sign In",
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    val request = LoginRequest(email, password)
                    viewModel.login(request) { isSuccess ->
                        if (isSuccess) {
                            onLoginClick()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            isLoading = authState is AuthState.Loading,
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sign Up link
        AuthFooterLink(
            normalText = "Don't have an account? ",
            linkText = "Sign Up",
            onClick = onNavigateToRegister,
            isDarkMode = isDarkMode
        )
    }
}