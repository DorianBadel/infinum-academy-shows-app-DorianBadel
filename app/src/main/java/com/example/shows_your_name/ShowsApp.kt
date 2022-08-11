package com.example.shows_your_name

import android.app.Application
import com.example.shows_your_name.database.ReviewEntity
import com.example.shows_your_name.database.ShowEntity
import com.example.shows_your_name.database.ShowsRoomDatabase
import com.example.shows_your_name.database.UserTypeConverter
import com.example.shows_your_name.models.User
import java.util.concurrent.Executors

class ShowsApp: Application() {

    val IS_REMEMBERED = "IS_REMEMBERED"
    val REMEMBERED_USER = "REMEMBERED_USER"
    val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"
    val ctLogoutAlertTitle = "You will leave your shows behind"
    val ctLogoutAlertDescription = "Are you sure you want to log out?"
    val ctLogoutAlertNegativeText = "No"
    val ctLogoutAlertPossitiveText = "Yes"
    val ctUser = "User"
    val ctUsername = "Username"
    val ctHideOff = "Hide"
    val ctHideOn = "Show"
    val ctImage = "Image"
    val ctExtrasData = "data"
    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"
    val utc = UserTypeConverter()

    val database by lazy {
        ShowsRoomDatabase.getDatabase(this)
    }

    /*private val shows = listOf(
        ShowEntity(999,"3".toFloat(),"This is a description","https://flxt.tmsimg.com/assets/p185008_b_h10_ai.jpg",3,"The Office"),
        ShowEntity(998,"5".toFloat(),"This is also a description","https://www.clementoni.com/media/prod/es/39543/stranger-things-2-1000-piezas-stranger-things_cxPTJ8S.jpg",2,"Stranger Things"),
        ShowEntity(997,"1".toFloat(),"This is the last description","https://yt3.ggpht.com/ytc/AMLnZu9APCgu8p6Tkhd1mKpAL-UC_MYUQ_JD4qA81w4sPA=s900-c-k-c0x00ffffff-no-rj",1,"Trailer Park Boys")
    )
    private val reviews = listOf(
        ReviewEntity(1,"I love the office so much",5,999,utc.toUserJson(User("1","mateas@gmail.com",null))),
        ReviewEntity(2,"The camera angles make me disy",1,999,utc.toUserJson(User("2","mateo@gmail.com",null))),
        ReviewEntity(3,"Its funny somethimes",3,999,utc.toUserJson(User("3","marko@gmail.com",null))),

        ReviewEntity(4,"Lovely show",5,998,utc.toUserJson(User("4","stjep@gmail.com",null))),
        ReviewEntity(5,"So inovative!!",5,998,utc.toUserJson(User("5","stjepan@gmail.com",null))),

        ReviewEntity(6,"This show is discusting!",997,3,utc.toUserJson(User("6","caren@gmail.com",null)))
    )*/
    private val showsList: List<ShowEntity> = listOf()
    private val reviewsList: List<ReviewEntity> = listOf()

    override fun onCreate() {
        super.onCreate()
        Executors.newSingleThreadExecutor().execute{
            database?.ShowDAO()?.insertAllShows(showsList)
            database?.ReviewDAO()?.insertAllReviews(reviewsList)
        }
    }
}
