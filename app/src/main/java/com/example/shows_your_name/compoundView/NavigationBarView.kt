package com.example.shows_your_name.compoundView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.example.shows_your_name.R
import com.example.shows_your_name.databinding.NavigationBarBinding
import com.google.android.material.navigation.NavigationView
import java.io.ByteArrayOutputStream

class NavigationBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    ) : NavigationView(context,attrs,defStyleAttr){

        private val sharedPrefs = "SHARED_STORAGE"
        private val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"
        private val ctOnline = "Online"
        private val ctOffline = "Offline"
        lateinit var binding: NavigationBarBinding

        init{
            binding = NavigationBarBinding.inflate(LayoutInflater.from(context),this)

            binding.txtInternetStatus.setPadding(
                context.resources.getDimensionPixelSize(R.dimen.offset_2x),
                context.resources.getDimensionPixelSize(R.dimen.offset_1x),
                0,0
            )

            setProfileImage()

            if(hasInternet()) binding.txtInternetStatus.text = ctOnline
            else binding.txtInternetStatus.text = ctOffline

        }

    private fun setProfileImage() = with(binding) {
        val encoded = getStringFromBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_new_profile
            )
        )

        val sharedPreferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val profilePhoto = sharedPreferences.getString(REMEMBERED_PHOTO, encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)

        Glide.with(context)
            .load(BitmapFactory.decodeByteArray(decoded,0,decoded.size))
            .circleCrop()
            .into(binding.btnProfile)
    }

    private fun getStringFromBitmap(bitmap: Bitmap): String {
        val byteArr = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
        val base64 = byteArr.toByteArray()
        return Base64.encodeToString(base64, Base64.DEFAULT)
    }

    private fun hasInternet(): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}