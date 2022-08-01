package com.example.shows_your_name.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShowDAO {
    @Query("SELECT * FROM showTable")
    fun getAllShows() : LiveData<List<ShowEntity>>

    @Query("SELECT * FROM showTable WHERE id IS :showID")
    fun getShow(showID: Int): LiveData<ShowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllShows(shows: List<ShowEntity>)
}