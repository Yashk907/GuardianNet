package com.example.guardiannetapp.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guardiannet.Login.SignInScreen
import com.example.guardiannetapp.Screens.GuardianScreens.GuardianSidePatientDetailsScreen
import com.example.guardiannetapp.Screens.GuardianScreens.GuardiansideControlScreen
import com.example.guardiannetapp.Screens.SignUpScreen.RegistrationScreen
import com.example.guardiannetapp.Screens.SignUpScreen.SignUpScreen
import com.example.guardiannetapp.Viewmodels.AuthViewModel

@Composable
fun Navigation(navHostController: NavHostController,
               modifier: Modifier = Modifier) {

    NavHost(navController = navHostController, startDestination = Screen.SIGNIN.name){

        composable (route = Screen.SPLASHSCREEN.name){

        }

        //register screens
        composable(route= Screen.REGISTERROLESCREEN.name) {
            RegistrationScreen(onPatientClick = {
                navHostController.navigate("${Screen.SIGNUP.name}/Patient")
            },
                onCaretakerClick = {
                    navHostController.navigate("${Screen.SIGNUP.name}/Guardian")
                })
        }

        composable(route = "${Screen.SIGNUP.name}/{RegisterRole}") {
            backstackEntry->
            val role = backstackEntry.arguments?.getString("RegisterRole")?:"Patient"
            SignUpScreen(navHostController,role,hiltViewModel())
        }

        composable(route = Screen.SIGNIN.name) {
            SignInScreen(hiltViewModel(), onSignUpClick = {navHostController.navigate(Screen.REGISTERROLESCREEN.name)}, navController = navHostController)
        }

        //guardian
        composable (route = "${Screen.GUARDIANCONTROLSCREEN.name}/{userId}"){
            backstackEntry->
            val userId = backstackEntry.arguments?.getString("userId")
            GuardiansideControlScreen(userId.toString(),navHostController)
        }

        composable(route = "${Screen.PATIENTDETAILSCREEN.name}/{patientId}") {
            backstackEntry ->
            val patientId = backstackEntry.arguments?.getString("patientId")
            GuardianSidePatientDetailsScreen(hiltViewModel(),patientId.toString())
    }

        //patient
        composable (route = Screen.PATIENTCONTROLSCREEN.name){

        }

    }
}

enum class Screen{
     //register screens
    SIGNUP,
    REGISTERROLESCREEN,

    //loginscreens
    SIGNIN,
    SPLASHSCREEN,

    //Gurdian
    GUARDIANCONTROLSCREEN,
    PATIENTDETAILSCREEN,

    //patients
    PATIENTCONTROLSCREEN,
}
