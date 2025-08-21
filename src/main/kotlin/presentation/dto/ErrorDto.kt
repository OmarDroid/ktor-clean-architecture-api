package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for API error responses.
 *
 * Provides structured error information for API clients including
 * error codes for programmatic handling and human-readable messages.
 * Optional details can provide additional context for debugging.
 *
 * @property code Machine-readable error code for programmatic handling (e.g., "USER_NOT_FOUND")
 * @property message Human-readable error message for display purposes
 * @property details Optional additional error context as key-value pairs for debugging
 */
@Serializable
data class ErrorDto(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null,
)
