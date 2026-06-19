package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelbooking.app.R
import com.hotelbooking.app.data.model.LoginRequest
import com.hotelbooking.app.ui.screens.auth.components.*
import com.hotelbooking.app.ui.theme.AppColors

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

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(
                context,
                authState.message.ifEmpty { "Login failed" },
                Toast.LENGTH_LONG
            ).show()
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
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sign in to book your next stay",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    ) {
        // Form section label
        Text(
            text = "Sign In",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary(isDarkMode),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        LuxuryTextField(
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
                        tint = AppColors.SkyBrand,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot password?",
                color = AppColors.SkyBrand,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LuxuryPrimaryButton(
            text = "Sign In",
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(LoginRequest(email = email.trim(), password = password)) { isSuccess ->
                        if (isSuccess) onLoginClick()
                    }
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            isLoading = authState is AuthState.Loading,
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthFooterLink(
            normalText = "Don't have an account? ",
            linkText = "Sign Up",
            onClick = onNavigateToRegister,
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
