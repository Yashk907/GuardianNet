package com.example.guardiannetapp.Screens.PatientScreens.HomeScreen

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center.position
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng



data class PatientHomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLocationName: String = "VIIT College",
    val userName: String = "Yash Karande",
    val userPhotoUrl: String = "https://randomuser.me/api/portraits/men/32.jpg",
    val userCode: String = "6x278y5"
)


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun PatientHomeScreen(
    uiState: PatientHomeUiState,
    onHelpClick: () -> Unit = {},
    onTakeMeHomeClick: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(18.4575, 73.8500), // VIIT College (Kondhwa) as center
            15f,
            0f,
            0f
        )
    }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // USER INFO
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(uiState.userPhotoUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = uiState.userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A2540)
                )
                Row {
                    Text(text = "my code: ", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = uiState.userCode,
                        fontSize = 14.sp,
                        color = Color(0xFF2F80ED),
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Current location: ${uiState.currentLocationName}",
            fontSize = 14.sp,
            color = Color(0xFF0A2540)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onHelpClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEB5757)),
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
            ) {
                Text("🔔 Help", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onTakeMeHomeClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
            ) {
                Text("📍 Take me Home", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
