package com.omaroid.domain.entities

import kotlinx.datetime.Instant
import kotlin.text.contains
import kotlin.text.isNotBlank

/**
 * Domain entities for the user management system.
 *
 * This package contains the core domain entities that represent the business
 * objects in the user management domain, including value objects with built-in
 * validation and type safety.
 */
/**
 * Core user domain entity representing a user in the system.
 *
 * This is the central domain entity that encapsulates all user-related data
 * with proper type safety through value objects and immutability.
 *
 * @property id Unique user identifier (positive long value)
 * @property email User's email address (validated format with @ symbol)
 * @property name User's display name (non-blank string)
 * @property createdAt Timestamp when the user was first created
 * @property updatedAt Timestamp when the user was last modified
 *
 * @see UserId
 * @see Email
 */
data class User(
    val id: UserId,
    val email: Email,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

/**
 * Value object representing a unique user identifier.
 *
 * Provides type safety for user IDs and ensures only positive values are allowed.
 * Uses @JvmInline for zero-cost abstraction while maintaining type safety.
 *
 * @property value The underlying long value representing the user ID
 * @throws IllegalArgumentException if the value is not positive (> 0)
 */
@JvmInline
value class UserId(val value: Long) {
    init {
        require(value > 0) { "User ID must be positive" }
    }
}

/**
 * Value object representing a validated email address.
 *
 * Provides basic email validation by ensuring the presence of the @ symbol
 * and non-blank content. Uses @JvmInline for zero-cost abstraction while
 * maintaining type safety and validation.
 *
 * @property value The underlying string value representing the email address
 * @throws IllegalArgumentException if the email is blank or doesn't contain @
 */
@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        require(value.contains("@")) { "Email must contain @ symbol" }
    }
}

