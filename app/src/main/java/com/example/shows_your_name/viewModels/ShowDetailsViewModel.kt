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
import java.util.concurrent.Executors

class ShowDetailsViewModel(
    private val database: ShowsRoomDatabase
) : ViewModel() {
    private val _listOfReviewsLiveData = MutableLiveData<List<ReviewApi>>()
    private val _showInfo = MutableLiveData<ShowApi>()
    private val _title = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()
    private val _imageId = MutableLiveData<String>()
    private var _username = MutableLiveData<String>()
    private var _reviewId = MutableLiveData<Int>()

    /*val title: LiveData<String> = _title
    val description: LiveData<String> = _description
    val imageId: LiveData<String> = _imageId
    val username: LiveData<String> = _username*/

    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"
    val ctShowId = "showId"

    val listOfReviewsLiveData: LiveData<List<ReviewApi>> = _listOfReviewsLiveData
    val showInfo: LiveData<ShowApi> = _showInfo


    fun getReviewsList(): LiveData<List<ReviewApi>>{
        return _listOfReviewsLiveData
    }

    fun getReviewsOffline(id: Int): LiveData<List<ReviewEntity>>{
        return database.ReviewDAO().getAllReviews(id)
    }
    fun getShowInfoOffline(id: Int): LiveData<ShowEntity>{
        return database.ShowDAO().getShow(id)
    }

    fun getShowInfoOnline(): LiveData<ShowApi>{
        return _showInfo
    }

    fun getShowInfo(id: Int,accessToken: String,client: String,UID: String,tokenType: String){
        val url = "/shows/${id}"
        ApiModule.retrofit.getShow(
            url,
            accessToken,
            client,
            UID,
            tokenType
        )
            .enqueue(object: Callback<ShowResponse> {
                override fun onFailure(call: Call<ShowResponse>, t: Throwable) {
                    /*getReviewsOffline(id)
                    getShowInfoOffline(id)*/
                }

                override fun onResponse(
                    call: Call<ShowResponse>,
                    response: Response<ShowResponse>
                ) {
                    _showInfo.value = response.body()?.show
                }
            })

    }

    fun updateDB(list: List<ReviewEntity>){
        Executors.newSingleThreadExecutor().execute {
            database.ReviewDAO().insertAllReviews(list)
        }
    }

    fun getReviews(id: Int,accessToken: String,client: String,UID: String,tokenType: String){

        val url = "/shows/${id}/reviews"
        ApiModule.retrofit.getReviews(
            url,
            accessToken,
            client,
            UID,
            tokenType
        )
            .enqueue(object: Callback<ReviewsResponse> {
                override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                    getReviewsOffline(id)
                    getShowInfoOffline(id)
                }

                override fun onResponse(
                    call: Call<ReviewsResponse>,
                    response: Response<ReviewsResponse>
                ) {
                    _listOfReviewsLiveData.value = response.body()?.reviews
                }
            })

    }

    fun addReview(
        id: Int,
        accessToken: String,client: String,UID: String,tokenType: String,
        reviewRating: Int, reviewComment: String
    ){
        val addReviewReq = addReviewRequest(
            rating = reviewRating,
            comment = reviewComment,
            showId = id
        )
        ApiModule.retrofit.addReview(addReviewReq,
            accessToken,
            client,
            UID,
            tokenType
        )
            .enqueue(object: Callback<AddReviewResponse> {
                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    getReviewsOffline(id)
                    getShowInfoOffline(id)
                }

                override fun onResponse(
                    call: Call<AddReviewResponse>,
                    response: Response<AddReviewResponse>
                ) {
                    getReviewsList()
                }
            })
    }
}
