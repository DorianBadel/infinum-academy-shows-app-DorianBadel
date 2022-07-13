package com.example.shows_your_name

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.example.shows_your_name.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginbtn.isEnabled = false

        binding.emailText.doAfterTextChanged {
            binding.loginbtn.isEnabled =
                android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailText.text.toString()).matches()
        }

        binding.loginbtn.setOnClickListener{
            val intent = Intent(this,Activity2::class.java)
            intent.putExtra("Username",binding.emailText.text.toString().substringBefore("@"))
            startActivity(intent)
        }
    }
}