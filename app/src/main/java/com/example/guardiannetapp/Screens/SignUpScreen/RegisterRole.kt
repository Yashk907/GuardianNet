package com.example.guardiannetapp.Screens.SignUpScreen


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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardiannetapp.R

// Main Registration Screen Composable
@Preview(showSystemUi = true)
@Composable
fun RegistrationScreen(
    onPatientClick: () -> Unit = {},
    onCaretakerClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Logo and App Name Section
        Image(
            painter = painterResource(R.drawable.guardiannetlogo),
            contentDescription = "App Logo",
            modifier = Modifier.height(100.dp).width(100.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Registration Options Section
        RegistrationOptionsSection(
            onPatientClick = onPatientClick,
            onCaretakerClick = onCaretakerClick
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

// Registration Options Section
@Composable
fun RegistrationOptionsSection(
    onPatientClick: () -> Unit,
    onCaretakerClick: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Register as",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF2C2C2C),
                modifier = Modifier.padding(16.dp)
            )

            RegistrationOptionCard(
                icon = Icons.Default.Person,
                title = "Patient",
                onClick = onPatientClick
            )

            RegistrationOptionCard(
                icon = Icons.Default.Shield,
                title = "Caretaker/Guardian",
                onClick = onCaretakerClick
            )
        }
    }
}

// Reusable Registration Option Card Component
@Composable
fun RegistrationOptionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.padding(vertical = 5.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF0A2540),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color(0xFF666666),
                modifier = Modifier.size(24.dp)
            )
        }
    }

}