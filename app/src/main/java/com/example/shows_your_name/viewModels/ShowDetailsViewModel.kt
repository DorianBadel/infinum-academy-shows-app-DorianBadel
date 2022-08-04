package com.example.shows_your_name.viewModels

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_your_name.Review
import com.example.shows_your_name.ReviewsAdapter
import com.example.shows_your_name.ShowDetailsFragment
import com.example.shows_your_name.database.ReviewEntity
import com.example.shows_your_name.database.ShowEntity
import com.example.shows_your_name.database.ShowsRoomDatabase
import com.example.shows_your_name.databinding.DialogAddReviewBinding
import com.example.shows_your_name.databinding.FragmentShowDetailsBinding
import com.example.shows_your_name.models.*
import com.example.shows_your_name.newtworking.ApiModule
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel(
    private val database: ShowsRoomDatabase
) : ViewModel() {
    private val _listOfReviewsLiveData = MutableLiveData<List<ReviewApi>>()
    /*private val _listOfReviewsLiveData1 = MutableLiveData<List<Review>>()
    private val _listOfReviewsLiveData2 = MutableLiveData<List<Review>>()
    private val _listOfReviewsLiveData3 = MutableLiveData<List<Review>>()*/
    private val _id = MutableLiveData<Int>()
    private val _title = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()
    private val _imageId = MutableLiveData<String>()
    private var _username = MutableLiveData<String>()

    val id: LiveData<Int> = _id
    val title: LiveData<String> = _title
    val description: LiveData<String> = _description
    val imageId: LiveData<String> = _imageId
    val username: LiveData<String> = _username
    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"
    val listOfReviewsLiveData: LiveData<List<ReviewApi>> = _listOfReviewsLiveData



    fun initValues(id: Int){
        _id.value = id
    }

    fun getReviewsList(): LiveData<List<ReviewApi>>{
        return _listOfReviewsLiveData
    }

    fun getReviewsOffline(id: Int): LiveData<List<ReviewEntity>>{
        //TODO filter the reviews based on id
        return database.ReviewDAO().getAllReviews()
    }
    fun getShowInfoOffline(id: Int): LiveData<ShowEntity>{
        //showID needs to be public
        return database.ShowDAO().getShow(id)
    }



    fun getReviews(ID: Int,accessToken: String,client: String,UID: String,tokenType: String){

        val url = "/shows/${ID}/reviews"
        ApiModule.retrofit.getReviews(
            url,
            accessToken,
            client,
            UID,
            tokenType
        )
            .enqueue(object: Callback<ReviewsResponse> {
                override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                    getReviewsOffline(ID)
                    getShowInfoOffline(ID)
                }

                override fun onResponse(
                    call: Call<ReviewsResponse>,
                    response: Response<ReviewsResponse>
                ) {
                    _listOfReviewsLiveData.value = response.body()?.reviews
                }
            })

    }

    fun addReview(binding: FragmentShowDetailsBinding,bottomSheetDialog: DialogAddReviewBinding,fragment: ShowDetailsFragment){
        binding.progressbar.isVisible = true


        var sharedPreferences: SharedPreferences
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctAccessToken, Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctClient, Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctUid, Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctTokenType, Context.MODE_PRIVATE)

        val url = "/shows/${_id.value}/reviews"

        val addReviewReq = addReviewRequest(
            rating = bottomSheetDialog.reviewSetRating.rating.toInt(),
            comment = bottomSheetDialog.commentText.text.toString(),
            showId = _id.value!!
        )
        ApiModule.retrofit.addReview(addReviewReq,
            sharedPreferences.getString(ctAccessToken,"")!!,
            sharedPreferences.getString(ctClient,"")!!,
            sharedPreferences.getString(ctUid,"")!!,
            sharedPreferences.getString(ctTokenType,"")!!
        )
            .enqueue(object: Callback<AddReviewResponse> {
                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    if(binding.progressbar.isVisible) binding.progressbar.isVisible = false
                }

                override fun onResponse(
                    call: Call<AddReviewResponse>,
                    response: Response<AddReviewResponse>
                ) {
                    if(binding.progressbar.isVisible) binding.progressbar.isVisible = false
                    getReviewsList()
                }
            })
    }
}
