package com.example.shows_your_name.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowsResponse (
    @SerialName("shows") val shows: List<ShowApi>
    )

@Serializable
data class ShowApi(
    @SerialName("id") val id: Int,
    @SerialName("average_rating") val avgRating: Float?,
    @SerialName("description") val description: String?,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("no_of_reviews") val noOfReviews: Int,
    @SerialName("title") val title: String
)