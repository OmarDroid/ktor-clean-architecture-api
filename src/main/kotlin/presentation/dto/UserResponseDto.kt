package com.omaroid.presentation.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for user data in API responses.
 *
 * Represents the JSON structure used when returning user information
 * to API clients. Uses snake_case naming for timestamp fields to follow
 * common API conventions while maintaining camelCase for other fields.
 *
 * @property id The user's unique identifier
 * @property email The user's email address
 * @property name The user's display name
 * @property createdAt ISO-8601 timestamp of when the user was created (serialized as "created_at")
 * @property updatedAt ISO-8601 timestamp of when the user was last updated (serialized as "updated_at")
 *
 * @see com.omaroid.domain.entities.User
 * @see com.omaroid.presentation.mappers.UserMapper
 */
@Serializable
data class UserResponseDto(
    val id: Long,
    val email: String,
    val name: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
)
