package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for user update requests.
 *
 * Represents the JSON payload structure expected when updating existing users.
 * Currently supports updating the user's display name only, as email addresses
 * are considered immutable once set.
 *
 * @property name The new display name for the user (will be validated for non-blank content)
 *
 * @see com.omaroid.domain.usecases.UpdateUserUseCase
 */
@Serializable
data class UpdateUserRequestDto(
    val name: String,
)
