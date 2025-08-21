package com.omaroid.domain.repositories

/**
 * Service interface for system health monitoring and diagnostics.
 *
 * Provides health check capabilities to verify the operational status
 * of critical system components like database connectivity, external services,
 * and other dependencies.
 *
 * @see com.omaroid.data.health.DatabaseHealthService
 */
interface HealthService {

    /**
     * Performs a health check to verify system component availability.
     *
     * Executes diagnostic tests on critical system components to determine
     * if the service is ready to handle requests. This typically includes
     * database connectivity checks and other essential service validations.
     *
     * @return true if all health checks pass and the system is healthy, false otherwise
     */
    suspend fun checkHealth(): Boolean
}