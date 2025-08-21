package com.omaroid.data.health


import com.omaroid.domain.repositories.HealthService
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseHealthService(
    private val database: Database,
) : HealthService {
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
