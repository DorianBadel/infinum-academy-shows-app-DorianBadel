package com.example.shows_your_name.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowsResponse (
    @SerialName("shows") val shows: List<ShowApi>,
    @SerialName("meta") val meta: Meta
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

@Serializable
data class Meta(
    @SerialName("pagination") val pagination: Pagination
)

@Serializable
data class Pagination(
    @SerialName("count") val count: Int,
    @SerialName("page") val page: Int,
    @SerialName("items") val noOfItems: Int,
    @SerialName("pages") val pages: Int
)