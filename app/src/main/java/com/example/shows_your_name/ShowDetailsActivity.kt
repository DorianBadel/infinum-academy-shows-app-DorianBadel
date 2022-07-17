package com.example.shows_your_name

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shows_your_name.databinding.ActivityShowDetailsBinding

class ShowDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.showTitle.text = intent.extras?.getString("Title")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}