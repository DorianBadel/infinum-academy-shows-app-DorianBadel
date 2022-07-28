package com.example.shows_your_name.viewModels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_your_name.Review
import com.example.shows_your_name.ReviewsAdapter
import com.example.shows_your_name.databinding.DialogAddReviewBinding
import com.example.shows_your_name.databinding.FragmentShowDetailsBinding

class ShowDetailsViewModel : ViewModel() {

    private val _listOfReviewsLiveData1 = MutableLiveData<List<Review>>()
    private val _listOfReviewsLiveData2 = MutableLiveData<List<Review>>()
    private val _listOfReviewsLiveData3 = MutableLiveData<List<Review>>()
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


    val listOfReviewsLiveData1: LiveData<List<Review>> = _listOfReviewsLiveData1
    val listOfReviewsLiveData2: LiveData<List<Review>> = _listOfReviewsLiveData2
    val listOfReviewsLiveData3: LiveData<List<Review>> = _listOfReviewsLiveData3


    init{
        _listOfReviewsLiveData1.value = listOf(
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
        )
    }

    fun initiateViewModel(bundle: Bundle?){
      _id.value = bundle?.getInt("ID")
      _title.value = bundle?.getString("Title").toString()
      _description.value = bundle?.getString("Description").toString()
      _imageId.value = bundle?.getInt("Image")
      _username.value = bundle?.getString("Username")

    }

    fun getReviews(): List<Review>{
        return if (_id.value == 2) {
            _listOfReviewsLiveData2.value!!
        } else if (_id.value == 3) {
            listOfReviewsLiveData3.value!!
        } else {
            _listOfReviewsLiveData1.value!!
        }
    }

    private fun getAverageRating(reviews: List<Review>): Float {
        var total = 0
        for (i in reviews) {
            total += i.rating
        }

        return total.toFloat() / reviews.count()
    }

    fun addReview(bottomSheetBinding: DialogAddReviewBinding, adapter: ReviewsAdapter, reviews: List<Review>): Review {
        val rating = bottomSheetBinding.reviewSetRating.rating.toInt()
        val comment = bottomSheetBinding.commentText.text.toString()
        adapter.addItem(Review(reviews.last().ID + 1,_username.value!!, comment, rating))

        return Review(reviews.last().ID + 1, _username.value!!, comment, rating)

    }

    fun updateStatistics(binding: FragmentShowDetailsBinding,reviews: List<Review>){
        binding.reviewsText.text =
            reviews.count().toString() + " REVIEWS, " + getAverageRating(reviews) + " AVERAGE"
        binding.ratingBar.rating = getAverageRating(reviews)
    }

}
