package com.example.shows_your_name

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.graphics.toColor
import com.example.shows_your_name.database.ReviewEntity
import com.example.shows_your_name.database.ShowEntity
import com.example.shows_your_name.database.ShowsRoomDatabase
import okhttp3.internal.toHexString
import java.util.concurrent.Executors

class ShowsApp: Application() {

    val database by lazy {
        ShowsRoomDatabase.getDatabase(this)
    }
    private val showsList: List<ShowEntity> = listOf()
    private val reviewsList: List<ReviewEntity> = listOf()

    override fun onCreate() {
        super.onCreate()
        Executors.newSingleThreadExecutor().execute{
            database?.ShowDAO()?.insertAllShows(showsList)
            database?.ReviewDAO()?.insertAllReviews(reviewsList)
        }
    }
}
