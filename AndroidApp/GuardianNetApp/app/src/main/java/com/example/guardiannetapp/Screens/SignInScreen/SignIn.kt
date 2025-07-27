package com.example.guardiannet.Login

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.guardiannetapp.Navigation.Screen
import com.example.guardiannetapp.R
import com.example.guardiannetapp.Viewmodels.AuthViewModel
import com.example.guardiannetapp.ui.theme.Poppins
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//@Preview
@SuppressLint("ContextCastToActivity")
@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onSignUpClick: () -> Unit,
    // viewModel: LoginViewModel = koinViewModel(),
     navController: NavController
) {
    // val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val signInState by viewModel.signinState.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(signInState) {
        signInState?.let { result ->
            result.onSuccess {
                Toast.makeText(context, "signed In Successfully!", Toast.LENGTH_SHORT).show()
                isLoading = false
                val role = it.data.user.role
                when(role){
                    "Guardian" -> navController.navigate("${Screen.GUARDIANCONTROLSCREEN.name}/${it.data.user._id}"){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                    "Patient" -> navController.navigate(Screen.PATIENTCONTROLSCREEN.name){
                        popUpTo(0){inclusive =true}
                        launchSingleTop = true
                    }
                }
            }
            result.onFailure { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }
    }

    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFECEFF1))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Image(
            painter = painterResource(R.drawable.guardiannetlogo),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Welcome Back",
            fontFamily = Poppins,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A2540)
        )

        Text(
            text = "Sign in to your account",
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF607D8B),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            // Email Field
            InputField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            InputField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Remember Me & Forgot Password Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    // Custom checkbox
                    Text(
                        text = if (rememberMe) "☑" else "☐",
                        fontSize = 18.sp,
                        color = if (rememberMe) Color(0xFF0A2540) else Color(0xFF607D8B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Remember me",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color(0xFF607D8B)
                    )
                }

                TextButton(
                    onClick = {
                        // TODO: Navigate to forgot password screen
                        // navController.navigate("forgot_password")
                    }
                ) {
                    Text(
                        text = "Forgot Password?",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color(0xFF0A2540),
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    // Validate inputs
                    when {
                        email.isBlank() -> errorMessage = "Please enter your email address"
                        !isValidEmail(email) -> errorMessage = "Please enter a valid email address"
                        password.isBlank() -> errorMessage = "Please enter your password"
                        password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                        else -> {
                            errorMessage = ""
                            isLoading = true

                            viewModel.signIn(email,password)


                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A2540)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        "Sign In",
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }


        }

        Spacer(modifier = Modifier.height(30.dp))

        // Sign up link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account? ",
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color(0xFF607D8B)
            )
            Text(
                text = "Sign Up",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0A2540),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(enabled = true, onClick = onSignUpClick)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF607D8B),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = LocalTextStyle.current.copy(
                color = Color(0xFF0A2540),
                fontSize = 16.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE9EEF2), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}