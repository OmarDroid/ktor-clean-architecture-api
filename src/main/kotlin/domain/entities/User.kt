package com.omaroid.domain.entities

import kotlinx.datetime.Instant
import kotlin.text.contains
import kotlin.text.isNotBlank

data class User(
    val id: UserId,
    val email: Email,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@JvmInline
value class UserId(val value: Long) {
    init {
        require(value > 0) { "User ID must be positive" }
    }
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        require(value.contains("@")) { "Email must contain @ symbol" }
    }
}

