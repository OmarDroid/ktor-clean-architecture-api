package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for system health check responses.
 *
 * Provides comprehensive health status information including overall system
 * status, component-specific health indicators, and optional error details.
 * Used by monitoring systems, load balancers, and operations teams.
 *
 * @property status Overall system health status ("healthy" or "unhealthy")
 * @property timestamp ISO-8601 timestamp of when the health check was performed
 * @property database Database connectivity status ("connected" or "disconnected")
 * @property version Current application version for identification
 * @property error Optional error message when system is unhealthy
 *
 * @see com.omaroid.domain.repositories.HealthService
 * @see com.omaroid.presentation.routes.configureHealthRoutes
 */
@Serializable
data class HealthStatus(
    val status: String,
    val timestamp: String,
    val database: String,
    val version: String = "0.0.1",
    val error: String? = null,
)