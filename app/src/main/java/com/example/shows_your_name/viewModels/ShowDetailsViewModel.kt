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
import com.example.shows_your_name.databinding.DialogAddReviewBinding
import com.example.shows_your_name.databinding.FragmentShowDetailsBinding
import com.example.shows_your_name.models.ReviewApi
import com.example.shows_your_name.models.ReviewsResponse
import com.example.shows_your_name.models.ShowApi
import com.example.shows_your_name.models.ShowsResponse
import com.example.shows_your_name.newtworking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {
    private val _listOfReviewsLiveData = MutableLiveData<List<ReviewApi>>()
    /*private val _listOfReviewsLiveData1 = MutableLiveData<List<Review>>()
    private val _listOfReviewsLiveData2 = MutableLiveData<List<Review>>()
    private val _listOfReviewsLiveData3 = MutableLiveData<List<Review>>()*/
    private val _id = MutableLiveData<Int>()
    private val _title = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()
    private val _imageId = MutableLiveData<Int>()
    private var _username = MutableLiveData<String>()

    val id: LiveData<Int> = _id
    val title: LiveData<String> = _title
    val description: LiveData<String> = _description
    val imageId: LiveData<Int> = _imageId
    val username: LiveData<String> = _username
    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"

    val ctID = "ID"

    val listOfReviewsLiveData: LiveData<List<ReviewApi>> = _listOfReviewsLiveData

    /*val listOfReviewsLiveData1: LiveData<List<Review>> = _listOfReviewsLiveData1
    val listOfReviewsLiveData2: LiveData<List<Review>> = _listOfReviewsLiveData2
    val listOfReviewsLiveData3: LiveData<List<Review>> = _listOfReviewsLiveData3*/


    init{
        /*_listOfReviewsLiveData1.value = listOf(
            Review(1, "renato.ruric", "", 3),
            Review(2, "gan.dalf", "joooj pre dobrooooo", 5)
        )
        _listOfReviewsLiveData2.value = listOf(
            Review(1, "renato.ruric", "", 5),
            Review(2, "gan.dalf", "joooj pre dobrooooo", 5),
            Review(3, "mark.twain", "Sve u svemu savrseno", 5)
        )
        _listOfReviewsLiveData3.value = listOf(
            Review(1, "plenky", "meh", 1)
        )*/
    }

    fun getReviewsList(): LiveData<List<ReviewApi>>{
        return _listOfReviewsLiveData
    }

    fun initiateViewModel(bundle: Bundle?,binding: FragmentShowDetailsBinding,fragment: ShowDetailsFragment){
      _id.value = bundle?.getInt("ID")
      _title.value = bundle?.getString("Title").toString()
      _description.value = bundle?.getString("Description").toString()
      _imageId.value = bundle?.getInt("Image")
      _username.value = bundle?.getString("Username")

        getReviews(binding, fragment)


    }

    fun getReviews(binding: FragmentShowDetailsBinding, fragment: ShowDetailsFragment){
        binding.progressbar.isVisible = true


        var sharedPreferences: SharedPreferences
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctAccessToken, Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctClient, Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctUid, Context.MODE_PRIVATE)
        sharedPreferences = fragment.requireContext().getSharedPreferences(ctTokenType, Context.MODE_PRIVATE)

        val url = "/shows/${_id.value}/reviews"
        ApiModule.retrofit.getReviews(
            url,
            sharedPreferences.getString(ctAccessToken,"")!!,
            sharedPreferences.getString(ctClient,"")!!,
            sharedPreferences.getString(ctUid,"")!!,
            sharedPreferences.getString(ctTokenType,"")!!
        )
            .enqueue(object: Callback<ReviewsResponse> {
                override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                    if(binding.progressbar.isVisible) binding.progressbar.isVisible = false
                }

                override fun onResponse(
                    call: Call<ReviewsResponse>,
                    response: Response<ReviewsResponse>
                ) {
                    if(binding.progressbar.isVisible) binding.progressbar.isVisible = false
                    _listOfReviewsLiveData.value = response.body()?.reviews
                }
            })
        /*return if (_id.value == 2) {
            _listOfReviewsLiveData2.value!!
        } else if (_id.value == 3) {
            listOfReviewsLiveData3.value!!
        } else {
            _listOfReviewsLiveData1.value!!
        }*/

    }

    /*fun addReview(bottomSheetBinding: DialogAddReviewBinding, adapter: ReviewsAdapter, reviews: List<Review>): Review {
        val rating = bottomSheetBinding.reviewSetRating.rating.toInt()
        val comment = bottomSheetBinding.commentText.text.toString()
        adapter.addItem(Review(reviews.last().ID + 1,_username.value!!, comment, rating))

        return Review(reviews.last().ID + 1, _username.value!!, comment, rating)

    }*/

    fun updateStatistics(binding: FragmentShowDetailsBinding,arguments: Bundle?){
        binding.reviewsText.text =
            arguments?.getInt("noRatings").toString() + " REVIEWS, " + arguments?.getFloat("avgRating").toString() + " AVERAGE"
        binding.ratingBar.rating = arguments!!.getFloat("avgRating")
    }
}
