package com.example.guardiannetapp.Screens.GuardianScreens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardiannetapp.Models.Patient
import com.example.guardiannetapp.Models.PatientGuardianObject
import com.example.guardiannetapp.Viewmodels.GurdianViewmodels.GuardianSidePatientDetailsVM
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianSidePatientDetailsScreen(
    viewModel: GuardianSidePatientDetailsVM,
    patientId: String,
    userId: String,
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onInformCaretakerClick: () -> Unit = {},
    onTrackClick: () -> Unit = {}
) {
    val patient by viewModel.patient.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // For showing the radius input dialog
    var showRadiusDialog by remember { mutableStateOf(false) }
    var radiusInput by remember { mutableStateOf("") }

    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    // Request location permission on screen open
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Fetch patient data
    LaunchedEffect(patientId) {
        viewModel.fetchPatient(patientId) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // Dialog for radius input
    if (showRadiusDialog) {
        AlertDialog(
            onDismissRequest = { showRadiusDialog = false },
            title = { Text("Set Safe Zone Radius") },
            text = {
                Column {
                    Text("Enter radius in meters:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = radiusInput,
                        onValueChange = { radiusInput = it },
                        placeholder = { Text("e.g. 500") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val radiusValue = radiusInput.toIntOrNull()
                    if (radiusValue != null && radiusValue > 0) {
                        // Get Guardian's current location and set as Safe Zone
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                val coordinates = listOf(location.longitude, location.latitude)
                                viewModel.setSafeZone(
                                    userId = userId,
                                    patientId = patient.userId,
                                    coordinates = coordinates,
                                    radius = radiusValue,
                                    onSuccess = {
                                        Toast.makeText(context, "Safe Zone Updated!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showRadiusDialog = false
                    } else {
                        Toast.makeText(context, "Please enter valid radius!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Set")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRadiusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            PatientDetailsTopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreClick
            )
        },
        bottomBar = {
            PatientDetailsBottomBar(
                onInformCaretakerClick = onInformCaretakerClick,
                onTrackClick = onTrackClick,
                onSetSafeZoneClick = {
                    showRadiusDialog = true
                }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        if (isLoading.value) {
            // Loading UI
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                PatientProfileSection(patientDetails = patient)
                PatientInfoSection(patientDetails = patient)
                CaretakerDetailsSection(guardian = patient.guardians.firstOrNull { it.isPrimary })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsTopBar(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Patient Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.shadow(2.dp)
    )
}

// Patient Profile Section
@Composable
fun PatientProfileSection(patientDetails: Patient) {
    val statusColor = when (patientDetails.status) {
        "Safe" -> Color(0xFF10B981)
        "Breached" -> Color(0xFFF59E0B)
        "Emergency" -> Color(0xFFEF4444)
        else -> Color(0xFF10B981)
    }

    val statusBackgroundColor = when (patientDetails.status) {
        "Safe" -> Color(0xFFD1FAE5)
        "Breached" -> Color(0xFFFEF3C7)
        "Emergency" -> Color(0xFFFEE2E2)
        else -> Color(0xFFD1FAE5)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = patientDetails.userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = statusBackgroundColor,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = patientDetails.status,
                    fontSize = 14.sp,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// Patient Info Section
@Composable
fun PatientInfoSection(patientDetails: Patient) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Patient Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Divider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )

            DetailRow(
                icon = Icons.Default.LocationOn,
                label = "Address",
                value = patientDetails.address
            )
        }
    }
}

// Caretaker Details Section
@Composable
fun CaretakerDetailsSection(guardian: PatientGuardianObject?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0FDF4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Caretaker",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Caretaker Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Divider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )

            val caretaker = guardian?.guardian
            if (caretaker != null) {
                DetailRow(
                    icon = Icons.Default.Person,
                    label = "Name",
                    value = caretaker.userName
                )
                DetailRow(
                    icon = Icons.Default.LocationOn,
                    label = "Address",
                    value = caretaker.address
                )
                DetailRow(
                    icon = Icons.Default.Phone,
                    label = "Contact",
                    value = caretaker.address // Note: This should probably be caretaker.phone
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No caretaker assigned",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// Enhanced Detail Row with Icon
@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(20.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color(0xFF0F172A),
                lineHeight = 22.sp
            )
        }
    }
}

// Enhanced Bottom Bar
@Composable
fun PatientDetailsBottomBar(
    onInformCaretakerClick: () -> Unit,
    onTrackClick: () -> Unit,
    onSetSafeZoneClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onInformCaretakerClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDEECFF),
                    contentColor = Color(0xFF1E40AF)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Inform",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Inform",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onSetSafeZoneClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = "Safe Zone",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Safe Zone",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onTrackClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A2540)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Track",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Track",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}