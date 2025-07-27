package com.example.guardiannetapp.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class Guardian(
    @SerialName("_id")
    val id: String = "", // MongoDB _id

    val userId: String = "", // References User._id
    val patients: List<GuardianPatient> = emptyList(), // Patients array

    val createdAt: String? = null, // timestamps
    val updatedAt: String? = null
)

@Serializable
data class GuardianPatient(
    val patient: Patient, // References Patient._id
    val isPrimary: Boolean = false
)
