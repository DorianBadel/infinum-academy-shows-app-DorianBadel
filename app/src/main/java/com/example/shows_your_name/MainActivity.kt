package com.example.shows_your_name

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.edit
import com.example.shows_your_name.databinding.ActivityMainBinding
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val sharedPrefs = "SHARED_STORAGE"
    val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"
    val ctExtrasData = "data"

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /*var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val sharedPreferences = this.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        if(result.resultCode == Activity.RESULT_OK){

            sharedPreferences.edit{
                putString(REMEMBERED_PHOTO,getStringFromBitmap(result.data?.extras?.get(ctExtrasData) as Bitmap))
            }
        }
    }

    fun getStringFromBitmap(bitmap: Bitmap): String {
        val byteArr = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
        val base64 = byteArr.toByteArray()
        return Base64.encodeToString(base64, Base64.DEFAULT)
    }*/
}