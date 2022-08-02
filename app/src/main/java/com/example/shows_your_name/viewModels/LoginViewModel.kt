package com.example.shows_your_name.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shows_your_name.models.LoginRequest
import com.example.shows_your_name.models.LoginResponse
import com.example.shows_your_name.newtworking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application): AndroidViewModel(application){

    private val sharedPrefs = "SHARED_STORAGE"
    private val ctUsername = "Username"
    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"


    private val loginResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

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
                    var sp: SharedPreferences = getApplication<Application>().getSharedPreferences(sharedPrefs,Context.MODE_PRIVATE)

                    sp.edit{
                        putString(ctAccessToken,response.headers().get("access-token"))
                        putString(ctClient,response.headers().get(ctClient).toString())
                        putString(ctTokenType,"Bearer")
                        putString(ctUid,response.headers().get(ctUid).toString())
                        putString(ctUsername,email.substringBefore("@"))
                        commit()
                    }

                    //val bundle = bundleOf(ctUsername to binding.emailTexttxt.text.toString().substringBefore("@"))

                    //fragment.findNavController().navigate(R.id.to_showsFragment,bundle)

                    loginResultLiveData.value = response.isSuccessful
                }
            })

    }
}