package com.example.shows_your_name

import androidx.annotation.DrawableRes

data class Show(
    val ID: Int,
    val title: String,
    val desc: String,
    @DrawableRes val imageResourceId: Int
)
