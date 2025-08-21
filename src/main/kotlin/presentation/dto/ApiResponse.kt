package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDto? = null,
    val timestamp: String,
)
