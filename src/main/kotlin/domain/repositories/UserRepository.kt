package com.omaroid.domain.repositories

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId

interface UserRepository {
    suspend fun create(email: Email, name: String): User
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: Email): User?
    suspend fun update(user: User): User
    suspend fun deleteById(id: UserId): Boolean
    suspend fun findAll(offset: Int = 0, limit: Int = 10): List<User>
    suspend fun getTotalCount(): Int
}