package com.example.guardiannetapp.Models

import kotlinx.serialization.Serializable


import kotlinx.serialization.SerialName

@Serializable
data class Patient(
    @SerialName("_id")
    val id: String = "",

    val userId: String = "", // References User._id
    val userName: String = "",

    val safeZoneCenter: SafeZoneCenter = SafeZoneCenter(),
    val safeZoneRadius: Int = 1000,

    val guardians: List<PatientGuardian> = emptyList(),
    val linkCode: String? = null,

    val status: String = "Safe", // Safe, Breached, Emergency

    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class SafeZoneCenter(
    val type: String = "Point", // Always Point
    val coordinates: List<Double> = emptyList() // [longitude, latitude]
)

@Serializable
data class PatientGuardian(
    val guardian: String = "", // Guardian ID
    val isPrimary: Boolean = false
)
