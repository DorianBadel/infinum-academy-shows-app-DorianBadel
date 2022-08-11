package com.example.shows_your_name.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "showTable")
data class ShowEntity (
    @ColumnInfo(name = "id") @PrimaryKey val id: Int,
    @ColumnInfo(name = "average_rating") val averageRating: Float?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "no_of_reviews") val noOfReviews: Int,
    @ColumnInfo(name = "title") val title: String
    )