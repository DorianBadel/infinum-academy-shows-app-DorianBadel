package com.example.shows_your_name.viewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_your_name.database.ReviewEntity
import com.example.shows_your_name.database.ShowEntity
import com.example.shows_your_name.database.ShowsRoomDatabase
import com.example.shows_your_name.models.*
import com.example.shows_your_name.newtworking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class ShowDetailsViewModel(
    private val database: ShowsRoomDatabase
) : ViewModel() {
    private val _listOfReviewsLiveData = MutableLiveData<List<ReviewApi>>()
    private val _showInfo = MutableLiveData<ShowApi>()
    val listOfReviewsLiveData: LiveData<List<ReviewApi>> = _listOfReviewsLiveData
    val showInfo: LiveData<ShowApi> = _showInfo

    lateinit var ctAccessToken: String
    lateinit var ctClient: String
    lateinit var ctUid: String
    lateinit var ctTokenType: String

    fun initiateVariables(accessToken: String,client: String,uid: String,tokenType: String){
        ctAccessToken = accessToken
        ctClient = client
        ctUid = uid
        ctTokenType = tokenType
    }

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
                    getShowInfoOffline(id)
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
                    getReviews(id,accessToken,client,UID,tokenType)
                }
            })
    }
}
