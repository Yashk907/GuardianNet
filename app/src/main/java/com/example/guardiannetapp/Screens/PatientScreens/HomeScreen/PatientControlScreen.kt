package com.example.guardiannetapp.Screens.PatientScreens.HomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardiannetapp.R
import com.example.guardiannetapp.Screens.GuardianScreens.GuardianBottomNavItem
import com.example.guardiannetapp.Screens.GuardianScreens.GuardianBottomNavigation
import com.example.guardiannetapp.Screens.GuardianScreens.GuardianHomeScreen
import com.example.guardiannetapp.Screens.GuardianScreens.GuardianTopBar
import com.example.guardiannetapp.ui.theme.Poppins

data class GuardianBottomNavItem(
    val title: String,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String,
    val isSpecial: Boolean = false
)

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientSideControlScreen(
) {
    val selectTab = rememberSaveable { mutableStateOf("home") }
    val sampleState = PatientHomeUiState(
        isLoading = false,
        currentLocationName = "VIIT College",
        userName = "Yash Karande",
        userPhotoUrl = "https://randomuser.me/api/portraits/men/32.jpg",
        userCode = "6x278y5"
    )

    Scaffold(
        topBar = {PatientTopBar()},
        bottomBar = {PatientBottomBar(
            selectedTab = selectTab.value,
            onSelectTab = {selectTab.value=it}
        )},
        containerColor = Color(0xFFF5F5F7)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectTab.value) {
                "home" -> PatientHomeScreen(uiState = sampleState) { }
                "profile" -> {
                    Text("Profile Screen", modifier = Modifier.align(Alignment.Center))
                }

            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientTopBar(){
    TopAppBar(
        // modifier = Modifier.height(120.dp),
        title = {
            Image(
                painter = painterResource(id = R.drawable.guardiannetlogo), // Replace with your logo
                contentDescription = "Inpage Logo",
                modifier = Modifier.width(120.dp).height(34.dp),
                //contentScale = ContentScale.Crop
            )
        },
    )
}

@Composable
fun PatientBottomBar(
    selectedTab: String,
    onSelectTab: (String) -> Unit
){
    val items = listOf(
        GuardianBottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home, "home"),
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