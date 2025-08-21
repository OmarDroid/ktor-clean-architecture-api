package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequestDto(
    val name: String,
)
