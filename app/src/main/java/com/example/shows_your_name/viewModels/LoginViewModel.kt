package com.example.shows_your_name.viewModels

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
                    val tokenType = "Bearer"
                    val accessToken = response.headers().get("access-token")
                    val client = response.headers().get("client")
                    val uid = response.headers().get("uid")

                    val bundle = bundleOf(ctUsername to binding.emailTexttxt.text.toString().substringBefore("@"),
                    "tokenType" to tokenType,"accessToken" to accessToken,"client" to client, "uid" to uid)

                    fragment.findNavController().navigate(R.id.to_showsFragment,bundle)

                    loginResultLiveData.value = response.isSuccessful
                }
            })

    }
}