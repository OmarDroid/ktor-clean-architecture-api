package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null,
)
