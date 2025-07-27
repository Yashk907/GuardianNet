package com.example.guardiannetapp.Models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
