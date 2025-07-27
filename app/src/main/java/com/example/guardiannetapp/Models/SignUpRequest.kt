package com.example.guardiannetapp.Models

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)
