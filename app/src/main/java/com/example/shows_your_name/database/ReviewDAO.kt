package com.example.shows_your_name.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shows_your_name.Review

@Dao
interface ReviewDAO {
    @Query("SELECT * FROM reviewTable WHERE show_id IS :showID")
    fun getAllReviews(showID: Int) : LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM reviewTable")
    fun getReviews(): LiveData<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReviews(reviews: List<ReviewEntity>)
}
