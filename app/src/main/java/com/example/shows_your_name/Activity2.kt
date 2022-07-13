package com.example.shows_your_name

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shows_your_name.databinding.Activity2Binding
import com.example.shows_your_name.databinding.ActivityMainBinding

class Activity2 : AppCompatActivity() {
    lateinit var binding: Activity2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.welcomemsg.text = "Welcome, " + intent.extras?.getString("Username")

    }
}