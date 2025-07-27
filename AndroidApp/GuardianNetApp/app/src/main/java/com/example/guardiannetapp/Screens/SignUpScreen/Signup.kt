package com.example.guardiannetapp.Screens.SignUpScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import kotlin.text.isBlank
import kotlin.text.isNotEmpty

//@Preview(showSystemUi = true)
@SuppressLint("ContextCastToActivity")
@Composable
fun SignUpScreen(
    navController: NavController,
    Role : String,
    viewModel: AuthViewModel
) {
    // val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val signUpState by viewModel.signupState.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity
    LaunchedEffect(signUpState) {
        signUpState?.let { result ->
            result.onSuccess {response->
                Toast.makeText(context, "Registered Successfully!", Toast.LENGTH_SHORT).show()
                isLoading = false
               val role = response.data.user.role
                when(role){
                    "Guardian" -> navController.navigate(Screen.GUARDIANCONTROLSCREEN.name){
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFECEFF1))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(R.drawable.guardiannetlogo),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Create Account",
            fontFamily = Poppins,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A2540)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            // Name Field
            InputField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it },
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            InputField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field
            PhoneInputField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            InputField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            InputField(
                label = "Confirm Password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontFamily = Poppins,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Validate inputs
                    when {
                        name.isBlank() -> errorMessage = "Please enter your full name"
                        email.isBlank() -> errorMessage = "Please enter your email address"
                        !isValidEmail(email) -> errorMessage = "Please enter a valid email address"
                        phoneNumber.isBlank() -> errorMessage = "Please enter your phone number"
                        phoneNumber.length != 10 -> errorMessage = "Phone number must be 10 digits"
                        password.isBlank() -> errorMessage = "Please enter a password"
                        password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                        password != confirmPassword -> errorMessage = "Passwords do not match"
                        else -> {
                            errorMessage = ""
                            isLoading = true

                            // TODO: Implement registration when ViewModel is available
                            viewModel.signUp(
                                name = name,
                                email = email,
                                phone = phoneNumber,
                                password = password,
                                role = Role
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
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
                        "Create Account",
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
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
                .padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

@Composable
fun PhoneInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Phone Number",
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF607D8B),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE9EEF2), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "+91",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF607D8B),
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Divider(
                color = Color(0xFF607D8B),
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = value,
                onValueChange = { if (it.length <= 10) onValueChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(
                    color = Color(0xFF0A2540),
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}