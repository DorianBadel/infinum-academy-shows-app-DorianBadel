package com.example.shows_your_name.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shows_your_name.Show
import com.example.shows_your_name.viewModels.ShowDetailsViewModel
import com.example.shows_your_name.viewModels.ShowsViewModel
import java.lang.IllegalArgumentException

class ShowsViewModelFactory (
    val database: ShowsRoomDatabase?
    ): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShowsViewModel::class.java)){
            return ShowsViewModel(database!!) as T
        }
        throw IllegalArgumentException("Something wen't wrong UH, OH")
    }
    }
