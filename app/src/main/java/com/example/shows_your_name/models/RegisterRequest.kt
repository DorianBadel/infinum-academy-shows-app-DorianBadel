package com.example.shows_your_name.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest (
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String
)