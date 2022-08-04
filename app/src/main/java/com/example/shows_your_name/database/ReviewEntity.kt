package com.example.shows_your_name.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shows_your_name.models.User

@Entity(tableName = "reviewTable")
data class ReviewEntity (
    @ColumnInfo(name = "id") @PrimaryKey val id: Int,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "show_id") val showID: Int,
    @ColumnInfo(name = "user") val user: String
    )