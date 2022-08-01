package com.example.shows_your_name.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ShowEntity::class
        //TODO ReviewEntity::class
    ],
    version = 1
)
abstract class ShowsRoomDatabase: RoomDatabase(){

    companion object{
        @Volatile
        private var INSTANCE: ShowsRoomDatabase? = null

        fun getDatabase(context: Context): ShowsRoomDatabase? {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context,
                    ShowsRoomDatabase::class.java,
                    "show_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = database
                database
            }
        }
    }

    abstract fun ShowDAO()
}