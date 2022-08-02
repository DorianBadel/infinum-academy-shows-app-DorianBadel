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
    private val ctLogoutAlertTitle = "You will leave your shows behind"
    private val ctLogoutAlertDescription = "Are you sure you want to log out?"
    private val ctLogoutAlertNegativeText = "No"
    private val ctLogoutAlertPossitiveText = "Yes"
    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"


    private val _listOfShowsLiveData1 = MutableLiveData<List<ShowApi>>()
    val listOfShowsLiveData1: LiveData<List<ShowApi>> = _listOfShowsLiveData1

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _fragment = MutableLiveData<ShowsFragment>()
    val fragment: LiveData<ShowsFragment> = _fragment


    fun initiateViewModel(fragment: ShowsFragment,username: String){
        _username.value = username
        _fragment.value = fragment

    }

    //Change profile photo
    fun encodeString(resources: Resources): String{
        return getStringFromBitmap(BitmapFactory.decodeResource(resources, R.drawable.profile_ico))
    }

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
                    _listOfShowsLiveData1.value = listOf(ShowApi(0,1.toFloat(),"t","",1,"str"))
                    hideShows(binding)
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



    fun logOut(sharedPreferences: SharedPreferences, resources: Resources){
        sharedPreferences.edit {
            putBoolean(IS_REMEMBERED, false)
            putString(REMEMBERED_USER, "")
            putString(REMEMBERED_PHOTO,encodeString(resources))
        }
    }

    fun createAlert(resources: Resources, dialog: BottomSheetDialog){

        var builder = AlertDialog.Builder(_fragment.value!!.activity)
        var sharedPreferences: SharedPreferences

        builder.setTitle(ctLogoutAlertTitle)
            .setMessage(ctLogoutAlertDescription)
            .setCancelable(true)
            .setPositiveButton(ctLogoutAlertPossitiveText){_,_ ->

                sharedPreferences = _fragment.value!!.requireContext().getSharedPreferences(ctUser, Context.MODE_PRIVATE)
                sharedPreferences = _fragment.value!!.requireContext().getSharedPreferences(ctUsername, Context.MODE_PRIVATE)

                logOut(sharedPreferences,resources)

                _fragment.value!!.findNavController().navigate(R.id.to_loginFraagment)
                dialog.dismiss()

            }
            .setNegativeButton(ctLogoutAlertNegativeText){dialogInterface,it ->
                dialogInterface.cancel()
            }
            .show()
    }

    fun hasInternet(): Boolean{
        return false
    }
}
