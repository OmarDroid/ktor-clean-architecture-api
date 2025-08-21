package com.omaroid.domain.repositories

interface HealthService {
    suspend fun checkHealth(): Boolean
}