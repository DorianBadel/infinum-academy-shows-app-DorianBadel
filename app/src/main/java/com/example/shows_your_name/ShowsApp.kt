package com.example.shows_your_name

import android.app.Application
import com.example.shows_your_name.database.ReviewEntity
import com.example.shows_your_name.database.ShowEntity
import com.example.shows_your_name.database.ShowsRoomDatabase
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

    val database by lazy {
        ShowsRoomDatabase.getDatabase(this)
    }

    private val shows = listOf(
        ShowEntity(1,"3".toFloat(),"This is a description","https://flxt.tmsimg.com/assets/p185008_b_h10_ai.jpg",3,"The Office"),
        ShowEntity(2,"5".toFloat(),"This is also a description","https://www.clementoni.com/media/prod/es/39543/stranger-things-2-1000-piezas-stranger-things_cxPTJ8S.jpg",2,"Stranger Things"),
        ShowEntity(3,"1".toFloat(),"This is the last description","https://yt3.ggpht.com/ytc/AMLnZu9APCgu8p6Tkhd1mKpAL-UC_MYUQ_JD4qA81w4sPA=s900-c-k-c0x00ffffff-no-rj",1,"Trailer Park Boys")
    )
    private val reviews = listOf(
        ReviewEntity(1,"I love the office so much",5,1,1), //User("1","mateas@gmail.com","")),
        ReviewEntity(2,"The camera angles make me disy",1,1,2), //User("2","mateo@gmail.com","")),
        ReviewEntity(3,"Its funny somethimes",3,1,3), //User("3","marko@gmail.com","")),

        ReviewEntity(4,"Lovely show",5,2,4), //User("4","stjep@gmail.com","")),
        ReviewEntity(5,"So inovative!!",5,2,5), //User("5","stjepan@gmail.com","")),

        ReviewEntity(6,"This show is discusting!",1,3,6) //User("6","caren@gmail.com",""))
    )

    override fun onCreate() {
        super.onCreate()
        Executors.newSingleThreadExecutor().execute{
            database?.ShowDAO()?.insertAllShows(shows) //? shouldn't be there
        }
    }
}
