package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthStatus(
    val status: String,
    val timestamp: String,
    val database: String,
    val version: String = "0.0.1",
    val error: String? = null,
)