package com.example.shows_your_name.viewModels

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.R
import com.example.shows_your_name.ShowsApp
import com.example.shows_your_name.ShowsFragment
import com.example.shows_your_name.database.ShowEntity
import com.example.shows_your_name.database.ShowsRoomDatabase
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.databinding.FragmentShowsBinding
import com.example.shows_your_name.models.ShowApi
import com.example.shows_your_name.models.ShowsResponse
import com.example.shows_your_name.newtworking.ApiModule
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ShowsViewModel(
    private val database: ShowsRoomDatabase
) : ViewModel(){

    //Constants
    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"
    private val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"

    private val ctUser = "User"
    private val ctUsername = "Username"
    private val ctHideOff = "Hide"
    private val ctHideOn = "Show"
    private val ctImage = "Image"
    private val ctExtrasData = "data"
    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"



    private val _listOfShowsLiveData1 = MutableLiveData<List<ShowApi>>()
    val listOfShowsLiveData1: LiveData<List<ShowApi>> = _listOfShowsLiveData1

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _thisApp = MutableLiveData<ShowsApp>()
    val thisApp: LiveData<ShowsApp> = _thisApp


    fun initiateViewModel(username: String){
        _username.value = username
    }

    //Change profile photo
    fun encodeBitmapToString(data: Intent?): String{
        return getStringFromBitmap(data?.extras?.get(ctExtrasData) as Bitmap)
    }

    //Shows

    fun getListOfShows(): LiveData<List<ShowApi>>{
       return _listOfShowsLiveData1
    }

    fun getListOfShowsOffline(): LiveData<List<ShowEntity>>{
        return database.ShowDAO().getAllShows()
    }

    fun getAllShows(binding: FragmentShowsBinding,fragment: ShowsFragment){
        binding.progressbar.isVisible = true

        var sharedPreferences: SharedPreferences =
            fragment.requireContext().getSharedPreferences(ctAccessToken,Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctClient,Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctUid,Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctTokenType,Context.MODE_PRIVATE)

        ApiModule.retrofit.getShows(
            sharedPreferences.getString(ctAccessToken,"")!!,
            sharedPreferences.getString(ctClient,"")!!,
            sharedPreferences.getString(ctUid,"")!!,
            sharedPreferences.getString(ctTokenType,"")!!
        )
            .enqueue(object: Callback<ShowsResponse>{
                override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                    if(binding.progressbar.isVisible) binding.progressbar.isVisible = false
                    getListOfShowsOffline()
                }

                override fun onResponse(
                    call: Call<ShowsResponse>,
                    response: Response<ShowsResponse>
                ) {
                    if(binding.progressbar.isVisible) binding.progressbar.isVisible = false
                    _listOfShowsLiveData1.value = response.body()!!.shows
                    showShows(binding)
                }
            })
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

     fun showShows(binding: FragmentShowsBinding){
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
}
