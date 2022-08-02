package com.example.shows_your_name

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.shows_your_name.database.ShowsViewModelFactory
import com.example.shows_your_name.databinding.ActivityMainBinding
import com.example.shows_your_name.viewModels.ShowsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /*private val viewModel: ShowsViewModel by viewModels {
        ShowsViewModelFactory((application as ShowsApp).database)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}