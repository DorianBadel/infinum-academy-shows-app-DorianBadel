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
import java.util.concurrent.Executors

class ShowsViewModel(
    private val database: ShowsRoomDatabase
) : ViewModel(){

    //Constants
    private val ctExtrasData = "data"


    private val _listOfShowsLiveData1 = MutableLiveData<List<ShowApi>>()
    val listOfShowsLiveData1: LiveData<List<ShowApi>> = _listOfShowsLiveData1

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

    fun getAllShows(accessToken: String, client: String, UID: String, tokenType: String){

        ApiModule.retrofit.getShows(
            accessToken,
            client,
            UID,
            tokenType
        )
            .enqueue(object: Callback<ShowsResponse>{
                override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                    getListOfShowsOffline()
                }

                override fun onResponse(
                    call: Call<ShowsResponse>,
                    response: Response<ShowsResponse>
                ) {
                    _listOfShowsLiveData1.value = response.body()!!.shows
                }
            })
    }

    fun updateDB(shows: List<ShowEntity>){

        Executors.newSingleThreadExecutor().execute {
            database.ShowDAO().insertAllShows(shows)
        }
    }


    fun getStringFromBitmap(bitmap: Bitmap): String {
        val byteArr = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
        val base64 = byteArr.toByteArray()
        return Base64.encodeToString(base64, Base64.DEFAULT)
    }


}
