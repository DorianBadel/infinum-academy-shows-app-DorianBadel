package com.example.shows_your_name.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    @SerialName("user") val user: User
    )