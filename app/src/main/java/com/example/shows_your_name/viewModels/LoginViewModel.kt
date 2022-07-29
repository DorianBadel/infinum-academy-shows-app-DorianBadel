package com.example.shows_your_name.viewModels

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.LoginFraagment
import com.example.shows_your_name.R
import com.example.shows_your_name.databinding.FragmentLoginFraagmentBinding
import com.example.shows_your_name.models.LoginRequest
import com.example.shows_your_name.models.LoginResponse
import com.example.shows_your_name.newtworking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel(){

    private val ctUsername = "Username"
    val ctAccessToken = "accessToken"
    val ctClient = "client"
    val ctUid = "uid"
    val ctTokenType = "tokenType"


    private val loginResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getLoginResultsLiveData(): LiveData<Boolean> {
        return loginResultLiveData
    }

    fun onLoginButtonClicked(fragment: LoginFraagment, binding: FragmentLoginFraagmentBinding) {
        val loginRequest = LoginRequest(
            email = binding.emailTexttxt.text.toString(),
            password = binding.passwordTexttxt.text.toString()
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
                    var sp: SharedPreferences = fragment.requireContext().getSharedPreferences(ctTokenType,
                        Context.MODE_PRIVATE)

                    sp.edit{
                        putString(ctAccessToken,response.headers().get("access-token"))
                        putString(ctClient,response.headers().get(ctClient).toString())
                        putString(ctTokenType,"Bearer")
                        putString(ctUid,response.headers().get(ctUid).toString())
                        putString(ctUsername,binding.emailTexttxt.text.toString().substringBefore("@"))
                        commit()
                    }

                    val bundle = bundleOf(ctUsername to binding.emailTexttxt.text.toString().substringBefore("@"))

                    fragment.findNavController().navigate(R.id.to_showsFragment,bundle)

                    loginResultLiveData.value = response.isSuccessful
                }
            })

    }
}