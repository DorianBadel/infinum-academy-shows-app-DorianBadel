package com.example.shows_your_name.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddReviewResponse(
    @SerialName("review") val review: ReviewApi
)