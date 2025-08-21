package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for user creation requests.
 *
 * Represents the JSON payload structure expected when creating new users.
 * Contains all required fields for user creation with validation handled
 * by the domain layer.
 *
 * @property email The user's email address (will be validated for format and uniqueness)
 * @property name The user's display name (will be validated for non-blank content)
 *
 * @see com.omaroid.domain.usecases.CreateUserUseCase
 */
@Serializable
data class CreateUserRequestDto(
    val email: String,
    val name: String,
)
