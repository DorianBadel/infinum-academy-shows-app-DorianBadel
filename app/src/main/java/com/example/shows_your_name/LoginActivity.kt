package com.example.shows_your_name

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.example.shows_your_name.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginbtn.isEnabled = false
        var emailCorrect = false;
        var passCorrect = false;

        binding.emailTexttxt.doAfterTextChanged {
            emailCorrect =
                android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailTexttxt.text.toString()).matches()

            binding.loginbtn.isEnabled = emailCorrect && passCorrect
        }
        binding.passwordTexttxt.doAfterTextChanged {
            passCorrect = binding.passwordTexttxt.text.toString() != ""

            binding.loginbtn.isEnabled = emailCorrect && passCorrect
        }

        binding.loginbtn.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("Username",binding.emailTexttxt.text.toString().substringBefore("@"))
            startActivity(intent)
        }
    }
}