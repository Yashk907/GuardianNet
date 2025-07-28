package com.example.guardiannetapp.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class Guardian(
    @SerialName("_id")
    val id: String = "", // MongoDB _id
    val userName : String = "",
    val userId: String = "", // References User._id
    val patients: List<GuardianPatientObject> = emptyList(), // Patients array
    val address : String ="",

    val createdAt: String? = null, // timestamps
    val updatedAt: String? = null
)

@Serializable
data class GuardianPatientObject(
    val patient: GuardianPatient, // References Patient._id
    val isPrimary: Boolean = false
)

@Serializable
data class GuardianPatient(
    val _id: String = "",

    val userId: String = "", // References User._id
    val userName: String = "",

    val safeZoneCenter: SafeZoneCenter = SafeZoneCenter(),
    val safeZoneRadius: Int = 1000,

    val linkCode: String? = null,

    val status: String = "Safe", // Safe, Breached, Emergency

    val address : String ="",
    val createdAt: String? = null,
    val updatedAt: String? = null
)