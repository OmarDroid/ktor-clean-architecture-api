/**
 * Health monitoring services for system diagnostics.
 *
 * This package contains implementations of health check services that monitor
 * the operational status of critical system components like databases and
 * external dependencies.
 */
package com.omaroid.data.health


import com.omaroid.domain.repositories.HealthService
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Database health check service implementation.
 *
 * Provides concrete implementation of health checking specifically for database
 * connectivity. Executes lightweight database queries to verify that the
 * database connection is active and responsive.
 *
 * This service is essential for:
 * - Load balancer health checks
 * - Service readiness probes
 * - Monitoring and alerting systems
 * - Graceful degradation scenarios
 *
 * @property database The database instance to monitor
 *
 * @see HealthService
 * @see Database
 */
class DatabaseHealthService(
    private val database: Database,
) : HealthService {

    /**
     * Performs a database connectivity health check.
     *
     * Executes a minimal database query (`SELECT 1`) to verify that:
     * - Database connection is established
     * - Database is responsive
     * - No major database issues are preventing basic operations
     *
     * The check uses a suspended transaction on the IO dispatcher to avoid
     * blocking the calling thread and to handle potential database timeouts gracefully.
     *
     * @return true if database is accessible and responsive, false if any error occurs
     *
     * @see newSuspendedTransaction
     * @see Dispatchers.IO
     */
    override suspend fun checkHealth(): Boolean = try {
        newSuspendedTransaction(Dispatchers.IO, database) {
            // Execute a lightweight query to confirm connectivity
            transaction(database) { exec("SELECT 1") }
        }
        true
    } catch (_: Exception) {
        false
    }
}
