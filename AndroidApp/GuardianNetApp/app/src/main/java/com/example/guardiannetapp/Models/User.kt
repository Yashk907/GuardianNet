package com.example.guardiannetapp.Models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val _id: String? = null,     // MongoDB user ID
    val name: String,
    val email: String,
    val phone: String,
    val userType: String,        // e.g., "patient", "caretaker-primary"
    val linkedCode: String? = null,  // code to link patient-caretaker
    val createdAt: String? = null,
    val updatedAt: String? = null
)
