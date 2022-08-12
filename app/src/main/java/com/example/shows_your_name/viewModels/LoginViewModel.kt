package com.example.shows_your_name.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shows_your_name.database.UserTypeConverter
import com.example.shows_your_name.models.LoginRequest
import com.example.shows_your_name.models.LoginResponse
import com.example.shows_your_name.newtworking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application): AndroidViewModel(application){

    private lateinit var sharedPrefs: String
    private lateinit var ctUsername: String
    private lateinit var ctAccessToken: String
    private lateinit var ctClient: String
    private lateinit var ctUid: String
    private lateinit var ctTokenType: String
    private lateinit var strAccessToken: String
    private lateinit var strBearer: String
    private lateinit var rememberedUser: String
    val utc = UserTypeConverter()


    private val loginResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun initLoginViewModel(
        sharedPreferences: String,username: String,accessToken: String,
        client: String,uid: String,tokenType: String,
        strAccessToken: String,bearerStr: String,rememberedUser: String
    ){
        sharedPrefs = sharedPreferences
        ctUsername = username
        ctAccessToken = accessToken
        ctClient = client
        ctUid = uid
        ctTokenType = tokenType
        this.strAccessToken = strAccessToken
        strBearer = bearerStr
        this.rememberedUser = rememberedUser
    }

    fun getLoginResultsLiveData(): LiveData<Boolean> {
        return loginResultLiveData
    }

    fun onLoginButtonClicked(email: String,password: String) {
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )
        ApiModule.retrofit.login(loginRequest)
            .enqueue(object: Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loginResultLiveData.value = false
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val sp: SharedPreferences = getApplication<Application>().getSharedPreferences(sharedPrefs,Context.MODE_PRIVATE)

                    sp.edit{
                        putString(ctAccessToken, response.headers()[strAccessToken])
                        putString(ctClient, response.headers()[ctClient].toString())
                        putString(ctTokenType,strBearer)
                        putString(ctUid, response.headers()[ctUid].toString())
                        putString(ctUsername,email.substringBefore("@"))
                        putString(rememberedUser,
                            response.body()?.let { utc.toUserJson(it.user) })
                        commit()
                    }

                    response.body()?.user

                    loginResultLiveData.value = response.isSuccessful
                }
            })

    }
}