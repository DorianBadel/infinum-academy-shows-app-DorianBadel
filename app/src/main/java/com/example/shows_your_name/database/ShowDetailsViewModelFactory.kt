package com.example.shows_your_name.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shows_your_name.Show
import com.example.shows_your_name.viewModels.ShowDetailsViewModel
import java.lang.IllegalArgumentException

class ShowDetailsViewModelFactory (
    val database: ShowsRoomDatabase?
): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)){
            return ShowDetailsViewModel(database!!) as T
        }
        throw IllegalArgumentException("Something went wrong UH, OH")
    }
}