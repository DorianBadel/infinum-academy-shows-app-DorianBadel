package com.example.shows_your_name.compoundView

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Base64
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.edit
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shows_your_name.R
import com.example.shows_your_name.ShowsFragment
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.databinding.FragmentShowsBinding
import com.example.shows_your_name.databinding.NavigationBarBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import java.io.ByteArrayOutputStream

class NavigationBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    ) : NavigationView(context,attrs,defStyleAttr){

        private val sharedPrefs = "SHARED_STORAGE"
        private val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"
        private val ctUsername = "Username"
        lateinit var binding: NavigationBarBinding
        val IS_REMEMBERED = "IS_REMEMBERED"
        val REMEMBERED_USER = "REMEMBERED_USER"
        val ctLogoutAlertTitle = "You will leave your shows behind"
        val ctLogoutAlertDescription = "Are you sure you want to log out?"
        val ctLogoutAlertNegativeText = "No"
        val ctLogoutAlertPossitiveText = "Yes"
        //lateinit var activityResultLauncher: ActivityResultLauncher<Intent>



        init{
            binding = NavigationBarBinding.inflate(LayoutInflater.from(context),this)

            binding.txtInternetStatus.setPadding(
                context.resources.getDimensionPixelSize(R.dimen.offset_2x),
                context.resources.getDimensionPixelSize(R.dimen.offset_1x),
                0,0
            )

            setProfileImage()

            if(hasInternet()) binding.txtInternetStatus.text = "Online"
            else binding.txtInternetStatus.text = "Offline"

        }

    fun setProfileImage() = with(binding) {
        val encoded = getStringFromBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.profile_ico
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

    fun getStringFromBitmap(bitmap: Bitmap): String {
        val byteArr = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
        val base64 = byteArr.toByteArray()
        return Base64.encodeToString(base64, Base64.DEFAULT)
    }

    /*fun showProfileBottomSheet(){
        val dialog = BottomSheetDialog(context)

        val bottomSheetBinding = DialogProfileBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bottomSheetBinding.root)
        val sharedPreferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        setProfileImagePopup(bottomSheetBinding)

        bottomSheetBinding.txtUsername.text = sharedPreferences.getString(ctUsername,"")


        //taking photo
        bottomSheetBinding.btnChangeProfilePic.setOnClickListener {
            val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(takePictureIntent.resolveActivity(packageManager) != null){

                activityResultLauncher.launch(takePictureIntent)
            }
        }


        bottomSheetBinding.btnDialogLogOut.setOnClickListener{
            createAlert(dialog)
        }

        dialog.show()
    }

    fun setProfileImagePopup(binding: DialogProfileBinding){
        val encoded = getStringFromBitmap(
            BitmapFactory.decodeResource(resources,
                R.drawable.profile_ico
            ))
        val sharedPreferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val profilePhoto = sharedPreferences.getString(REMEMBERED_PHOTO, encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)

        Glide.with(this)
            .load(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
            .circleCrop()
            .into(binding.imgProfile)
    }

    //change photo and logout

    fun createAlert(dialog: BottomSheetDialog){

        var builder = AlertDialog.Builder(context)

        builder.setTitle(ctLogoutAlertTitle)
            .setMessage(ctLogoutAlertDescription)
            .setCancelable(true)
            .setPositiveButton(ctLogoutAlertPossitiveText){_,_ ->
                logOut()

                findNavController().navigate(R.id.to_loginFraagment)
                dialog.dismiss()

            }
            .setNegativeButton(ctLogoutAlertNegativeText){dialogInterface,it ->
                dialogInterface.cancel()
            }
            .show()
    }

    fun logOut(){
        val sharedPreferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(IS_REMEMBERED, false)
            putString(REMEMBERED_USER, "")
            remove(REMEMBERED_PHOTO)
        }
    }

    fun setProfileImage(binding: DialogProfileBinding,bindingMain: FragmentShowsBinding){
        val encoded = getStringFromBitmap(
            BitmapFactory.decodeResource(resources,
                R.drawable.profile_ico
            ))
        val sharedPreferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val profilePhoto = sharedPreferences.getString(REMEMBERED_PHOTO, encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)

        Glide.with(this)
            .load(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
            .circleCrop()
            .into(binding.imgProfile)


        //binding.imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
    }*/

    fun hasInternet(): Boolean{
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