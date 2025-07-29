package com.example.guardiannetapp.Screens.PatientScreens.HomeScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.guardiannetapp.Screens.PatientScreens.LocationUtils.LocationUtils
import com.example.guardiannetapp.Viewmodels.PatientViewModel.PatientHomeScreenVM
import com.example.guardiannetapp.services.LocationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.delay
import kotlin.jvm.java

data class PatientHomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLocationName: String = "VIIT College, Pune",
    val userName: String = "Yash Karande",
    val userPhotoUrl: String = "https://randomuser.me/api/portraits/men/32.jpg",
    val userCode: String = "6X278Y5",
    val safeZoneStatus: SafeZoneStatus = SafeZoneStatus.SAFE,
    val connectedGuardians: Int = 3,
    val batteryLevel: Int = 85
)

enum class SafeZoneStatus(val displayName: String, val color: Color) {
    SAFE("Safe Zone", Color(0xFF27AE60)),
    BREACH("Outside Zone", Color(0xFFE74C3C))
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PatientHomeScreen(
    userId : String,
    viewModel : PatientHomeScreenVM ,
    uiState: PatientHomeUiState = PatientHomeUiState(),
    onHelpClick: () -> Unit = {},
    onTakeMeHomeClick: () -> Unit = {},
    onCodeCopy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val patient = viewModel.patient.collectAsState()

    // Permissions
    val fineLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val backgroundLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    // Step 1: Request fine location first
    LaunchedEffect(fineLocationPermissionState.status.isGranted) {
        if (!fineLocationPermissionState.status.isGranted) {
            fineLocationPermissionState.launchPermissionRequest()
        }
    }

    // UI & Logic
    when {
        !fineLocationPermissionState.status.isGranted -> {
            Text("Location permission required")
        }

        fineLocationPermissionState.status.isGranted && !backgroundLocationPermissionState.status.isGranted -> {
            // Step 2: Request background location after fine location granted
            LaunchedEffect(backgroundLocationPermissionState.status.isGranted) {
                if (!backgroundLocationPermissionState.status.isGranted) {
                    backgroundLocationPermissionState.launchPermissionRequest()
                }
            }
            Text("Background location permission required")
        }

        fineLocationPermissionState.status.isGranted && backgroundLocationPermissionState.status.isGranted -> {
            // Step 3: Start service once all permissions granted
            LaunchedEffect(Unit) {
                viewModel.fetchPatient(
                    userId = userId,
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                ) { patientData ->   // now success callback works
                    val lat = patientData.safeZoneCenter.coordinates[0]
                    val lng = patientData.safeZoneCenter.coordinates[1]
                    val radius = patientData.safeZoneRadius

                    val intent = Intent(context, LocationService::class.java).apply {
                        putExtra("center_lat", lat)
                        putExtra("center_lng", lng)
                        putExtra("radius", radius)
                    }
                    ContextCompat.startForegroundService(context, intent)
                }
            }


        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(18.4575, 73.8500), // Example: VIIT Pune
            15f // Zoom level
        )
    }
    val scrollState = rememberScrollState()

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(600)
    )





    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE9ECEF)
                    )
                )
            )
    ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Status Header Card
                StatusHeaderCard(
                    uiState = uiState,
                    onCodeCopy = onCodeCopy,
                    modifier = Modifier.scale(cardScale)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Stats Row
                QuickStatsRow(
                    safeZoneStatus = uiState.safeZoneStatus,
                    connectedGuardians = uiState.connectedGuardians,
                    batteryLevel = uiState.batteryLevel
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                ActionButtonsSection(
                    onHelpClick = onHelpClick,
                    onTakeMeHomeClick = onTakeMeHomeClick,
                    safeZoneStatus = uiState.safeZoneStatus
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Location Card with Map
                LocationMapCard(
                    cameraPositionState = cameraPositionState,
                    currentLocationName = uiState.currentLocationName
                )

                Spacer(modifier = Modifier.height(20.dp))
            }


        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF0A2540))
            }
        }
    }
}

@Composable
private fun StatusHeaderCard(
    uiState: PatientHomeUiState,
    onCodeCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image with Status Ring
            Box {
                Image(
                    painter = rememberAsyncImagePainter(uiState.userPhotoUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(uiState.safeZoneStatus.color, CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Status",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome back,",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = uiState.userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A2540)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Code section with copy functionality
                Surface(
                    modifier = Modifier.clickable { onCodeCopy() },
                    color = Color(0xFF0A2540).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ID: ${uiState.userCode}",
                            fontSize = 13.sp,
                            color = Color(0xFF0A2540),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = Color(0xFF0A2540),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    safeZoneStatus: SafeZoneStatus,
    connectedGuardians: Int,
    batteryLevel: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Status",
            value = safeZoneStatus.displayName,
            color = safeZoneStatus.color,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "Guardians",
            value = "$connectedGuardians Active",
            color = Color(0xFF27AE60),
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "Battery",
            value = "$batteryLevel%",
            color = if (batteryLevel > 50) Color(0xFF27AE60) else Color(0xFFF39C12),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onHelpClick: () -> Unit,
    onTakeMeHomeClick: () -> Unit,
    safeZoneStatus: SafeZoneStatus
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emergency Help Button (priority based on status)
        val helpButtonColor = if (safeZoneStatus == SafeZoneStatus.BREACH) {
            Color(0xFFE74C3C)
        } else {
            Color(0xFFFF6B6B)
        }

        PrimaryActionButton(
            text = "🚨 Emergency Help",
            color = helpButtonColor,
            onClick = onHelpClick,
            modifier = Modifier.fillMaxWidth()
        )

        // Take Me Home Button
        PrimaryActionButton(
            text = "🏠 Take Me Home",
            color = Color(0xFF3498DB),
            onClick = onTakeMeHomeClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100)
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun LocationMapCard(
    cameraPositionState: CameraPositionState,
    currentLocationName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFF0A2540),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Current Location",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = currentLocationName,
                        fontSize = 16.sp,
                        color = Color(0xFF0A2540),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        isTrafficEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        compassEnabled = true,
                        myLocationButtonEnabled = true,
                        mapToolbarEnabled = false
                    )
                )

            }
        }
    }
}

@Composable
private fun PermissionRequestScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.shadow(12.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFF0A2540),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Location Access Needed",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A2540),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "We need access to your location to ensure your safety and provide emergency assistance when needed.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryActionButton(
                    text = "Grant Permission",
                    color = Color(0xFF0A2540),
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun PatientHomeScreenPreview() {
//    MaterialTheme {
//        PatientHomeScreen(
//            uiState = PatientHomeUiState(
//                userName = "Yash Karande",
//                userCode = "6X278Y5",
//                currentLocationName = "VIIT College, Pune",
//                safeZoneStatus = SafeZoneStatus.SAFE,
//                connectedGuardians = 3,
//                batteryLevel = 85
//            ),
//            onHelpClick = { },
//            onTakeMeHomeClick = { },
//            onCodeCopy = { }
//        )
//    }
//}