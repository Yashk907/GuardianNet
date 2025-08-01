package com.example.guardiannetapp.Screens.GuardianScreens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guardiannetapp.Models.GuardianPatient
import com.example.guardiannetapp.Models.GuardianPatientObject
import com.example.guardiannetapp.Models.Patient
import com.example.guardiannetapp.Viewmodels.GurdianViewmodels.GuardianHomeScreenVM
import com.example.guardiannetapp.services.GuardianLocationListenerService
import androidx.core.content.edit

//enum class PatientStatus(val displayName: String, val color: Color) {
//    SAFE("Safe", Color(0xFF4CAF50)),
//    BREACHED("Breached", Color(0xFFFF9800)),
//    EMERGENCY("Emergency", Color(0xFFF44336))
//}



@Composable
fun GuardianHomeScreen(
    userId : String,
    viewModel : GuardianHomeScreenVM,
    onPatientClick: (GuardianPatient) -> Unit = {},
    onAddPatientClick: () -> Unit = {},
    modifier: Modifier= Modifier
) {
    val isLoading = viewModel.isLoading.collectAsState()
    val guardian = viewModel.guardian.collectAsState()
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }


    LaunchedEffect(Unit) {
        Log.d("userId", userId)

        // Fetch Guardian and handle success
        viewModel.fetchGuardian(
            userId = userId,
            onError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                Log.d("guardian", it.toString())
            },
            onSuccess = { guardianId ->
                // Save guardianId to SharedPreferences
                context.getSharedPreferences("SafeZonePrefs", Context.MODE_PRIVATE)
                    .edit {
                        putString("guardianUserId", guardianId)
                    }

                // Start foreground service
                val intent = Intent(context, GuardianLocationListenerService::class.java)
                ContextCompat.startForegroundService(context, intent)
            }
        )
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F7)
    ) {
        if (isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading patients...", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // My Patients Section
                if (guardian.value.patients.isNotEmpty()) {
                    item { SectionHeader("My Patients (${guardian.value.patients.size})") }
                    items(guardian.value.patients) { patient ->
                        CompactPatientCard(patient = patient, onClick = { onPatientClick(patient.patient) })
                    }
                }

                // Nearby Help Requests Section
//                if (uiState.nearbyRequests.isNotEmpty()) {
//                    item { SectionHeader("Nearby Help Requests (${uiState.nearbyRequests.size})") }
//                    items(uiState.nearbyRequests) { patient ->
//                        CompactPatientCard(
//                            patient = patient,
//                            onClick = { onPatientClick(patient) },
//                            isNearbyRequest = true
//                        )
//                    }
//                }

                // Empty State
                if (guardian.value.patients.isEmpty() ) {
                    item { EmptyState(onAddPatientClick) }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CompactPatientCard(
    patient: GuardianPatientObject,
    onClick: () -> Unit,
    isNearbyRequest: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1F5FE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color(0xFF0288D1),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = patient.patient.userName ?: "Unknown Patient",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C2C2C),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isNearbyRequest && patient.isPrimary) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0A2540), RoundedCornerShape(6.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRIMARY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${patient.patient.safeZoneRadius}m safe zone",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(patient.patient.status)
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View Details",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {

    val color = when (status) {
        "Safe" -> Color(0xFF4CAF50)
        "Breached" -> Color(0xFFFF9800)
        "Emergency" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2C2C2C),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun EmptyState(onAddPatientClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No patients yet",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your first patient to start monitoring their safety",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAddPatientClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A2540))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Patient")
            }
        }
    }
}

