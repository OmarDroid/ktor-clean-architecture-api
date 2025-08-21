package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDto(
    val email: String,
    val name: String,
)
