package com.example.guardiannetapp.Models.Response

import android.os.Message
import com.example.guardiannetapp.Models.User

data class ApiResponse <T>(
    val message: String,
    val data : T
)

data class AuthData(
    val user: User,
    val token : String
)