package com.example.shows_your_name

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.edit
import androidx.core.view.accessibility.AccessibilityViewCommand
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.databinding.FragmentShowsBinding
import java.io.ByteArrayOutputStream

class ShowsViewModel : ViewModel(){

    //Constants
    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"
    private val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"
    private val ctUser = "User"
    private val ctUsername = "Username"
    private val ctImage = "Image"
    private val ctDescription = "Description"
    private val ctID = "ID"
    private val ctTitle = "Title"
    private val HAS_PHOTO = "HAS_PHOTO"
    private val ctHideOff = "Hide"
    private val ctHideOn = "Show"
    private val ctLogoutAlertTitle = "You will leave your shows behind"
    private val ctLogoutAlertDescription = "Are you sure you want to log out?"
    private val ctLogoutAlertNegativeText = "No"
    private val ctLogoutAlertPossitiveText = "Yes"
    private val ctExtrasData = "data"

    //The list of data
    private val _listOfShowsLiveData = MutableLiveData<List<Show>>()
    val listOfShowsLiveData: LiveData<List<Show>> = _listOfShowsLiveData

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username


    init {
        _listOfShowsLiveData.value = listOf(
            Show(1,"The Office","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_office),
            Show(2,"Stranger Things","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_stranger_things ),
            Show(3,"Krv nije voda","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_krv_nije_voda )
        )
    }

    fun initiateViewModel(arguments: Bundle?,binding: FragmentShowsBinding){
        _username.value = arguments?.getString(ctUsername).toString()
        showShows(binding)
    }

    fun getStringFromBitmap(bitmap: Bitmap): String {
        val byteArr = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
        val base64 = byteArr.toByteArray()
        return Base64.encodeToString(base64, Base64.DEFAULT)
    }

    fun showOrHideShows(binding: FragmentShowsBinding){
        if(binding.showHideShows.text == ctHideOn){
            showShows(binding)
        } else if (binding.showHideShows.text == ctHideOff){
            hideShows(binding)
        }
    }

    private fun showShows(binding: FragmentShowsBinding){
        binding.noShowsIco.isVisible = false
        binding.noShowsText.isVisible = false
        binding.showsRecycler.isVisible = true
        binding.showHideShows.text = ctHideOff
    }

    private fun hideShows(binding: FragmentShowsBinding){
        binding.noShowsIco.isVisible = true
        binding.noShowsText.isVisible = true
        binding.showsRecycler.isVisible = false
        binding.showHideShows.text = ctHideOn
    }

    fun setProfileImage(sharedPreferences: SharedPreferences,binding: DialogProfileBinding,resources: Resources){
        val encoded = getStringFromBitmap(BitmapFactory.decodeResource(resources, R.drawable.profile_ico))
        val profilePhoto = sharedPreferences.getString(REMEMBERED_PHOTO, encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)

        binding.imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
    }

    fun encodeString(resources: Resources): String{
        return getStringFromBitmap(BitmapFactory.decodeResource(resources, R.drawable.profile_ico))
    }

    fun encodeBitmapToString(data: Intent?): String{
        return getStringFromBitmap(data?.extras?.get(ctExtrasData) as Bitmap)
    }

    fun logOut(sharedPreferences: SharedPreferences, resources: Resources){
        sharedPreferences.edit {
            putBoolean(IS_REMEMBERED, false)
        }
        sharedPreferences.edit{
            putString(REMEMBERED_USER, "")
        }
        sharedPreferences.edit{
            putString(REMEMBERED_PHOTO,encodeString(resources))
        }
    }
}