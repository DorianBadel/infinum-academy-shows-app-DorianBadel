package com.example.shows_your_name.database

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface ReviewDAO {
    @Query("SELECT * FROM reviewTable")
    fun getAllReviews() : LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM reviewTable WHERE id IS :reviewID")
    fun getReview(reviewID: Int): LiveData<ReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReview(review: ReviewEntity)
}
