package com.example.guardiannetapp.Screens.GuardianScreens


import AddPatientScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guardiannetapp.Navigation.Screen
import com.example.guardiannetapp.R
import com.example.guardiannetapp.ui.theme.Poppins

// Bottom Navigation items
data class GuardianBottomNavItem(
    val title: String,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String,
    val isSpecial: Boolean = false
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardiansideControlScreen(
    userId : String,
    navController: NavController,
) {
    val selectTab = rememberSaveable { mutableStateOf("home") }
    val notificationCount = rememberSaveable { mutableStateOf(0) }
    val guardianName = rememberSaveable { mutableStateOf("Guardian") }
    val hasEmergencyAlert = rememberSaveable { mutableStateOf(false)}
    Scaffold(
        topBar = {
            GuardianTopBar(
                guardianName = guardianName.value,
                hasEmergencyAlert = hasEmergencyAlert.value,
                notificationCount = notificationCount.value,
                onNotificationClick = {  }
            )
        },
        bottomBar = {
            GuardianBottomNavigation(
                selectedTab = selectTab.value,
                onSelectTab = {selectTab.value=it}
            )
        },
        containerColor = Color(0xFFF5F5F7)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sample Data


            // Show screen based on selected route
            when (selectTab.value) {
                "home" -> GuardianHomeScreen(userId,hiltViewModel(),
                    onPatientClick = {
                        navController.navigate("${Screen.PATIENTDETAILSCREEN.name}/${it.id}")
                    },
                    modifier = Modifier.padding(paddingValues))
                "add_patient" -> AddPatientScreen(hiltViewModel(),userId, modifier = Modifier.padding(paddingValues))
                "profile" -> {
                    Text("Profile Screen", modifier = Modifier.align(Alignment.Center))
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianTopBar(
    guardianName: String,
    notificationCount: Int,
    hasEmergencyAlert: Boolean,
    onNotificationClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Logo
                Image(
                    painter = painterResource(R.drawable.guardiannetlogo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Welcome back,", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                    Text(
                        guardianName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C2C2C),
                        fontFamily = Poppins
                    )
                }
            }
        },
        actions = {
            // Notifications icon with badge
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge(
                            containerColor = if (hasEmergencyAlert) Color(0xFFF44336) else Color(0xFFFF9800)
                        ) {
                            Text(
                                text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = if (hasEmergencyAlert) Color(0xFFF44336) else Color(0xFF2C2C2C)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun GuardianBottomNavigation(
    selectedTab: String,
    onSelectTab: (String) -> Unit
) {
    val items = listOf(
        GuardianBottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home, "home"),
        GuardianBottomNavItem("Add Patient", Icons.Default.Add, Icons.Default.Add, "add_patient", true),
        GuardianBottomNavItem("Profile", Icons.Outlined.Person, Icons.Filled.Person, "profile")
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            if (item.isSpecial) {
                // Center Add Patient Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp, vertical = 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0A2540), shape = RoundedCornerShape(20.dp))
                            .clickable { onSelectTab(item.route) }
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = item.selectedIcon,
                                contentDescription = item.title,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            } else {
                val isSelected = selectedTab == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Poppins,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    },
                    selected = isSelected,
                    onClick = { onSelectTab(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A2540),
                        selectedTextColor = Color(0xFF0A2540),
                        unselectedIconColor = Color(0xFF607D8B),
                        unselectedTextColor = Color(0xFF607D8B),
                        indicatorColor = Color(0xFF0A2540).copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun ControlScreenPreview() {
//    var selectedTab by remember { mutableStateOf("home") }
//
//    MaterialTheme {
//        GuardiansideControlScreen(
//        )
//    }
//}
