/**
 * Data Transfer Objects (DTOs) for API communication.
 *
 * This package contains serializable data classes used for HTTP request/response
 * payloads, providing a clean separation between domain entities and external
 * API contracts. All DTOs use kotlinx.serialization for JSON marshalling.
 */
package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

/**
 * Generic wrapper for all API responses providing consistent response structure.
 *
 * Standardizes the format of all API responses with success indicators,
 * optional data payload, error details, and timestamps for tracking.
 * This ensures a consistent client experience across all endpoints.
 *
 * @param T The type of the data payload (can be any serializable type)
 * @property success Indicates whether the operation was successful
 * @property data The response payload when successful (null for errors)
 * @property error Error details when the operation fails (null for success)
 * @property timestamp ISO-8601 timestamp of when the response was generated
 *
 * @see ErrorDto
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDto? = null,
    val timestamp: String,
)
