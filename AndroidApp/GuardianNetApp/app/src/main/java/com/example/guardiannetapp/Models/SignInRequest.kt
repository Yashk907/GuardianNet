package com.example.guardiannetapp.Models

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email : String,
    val password : String
)
