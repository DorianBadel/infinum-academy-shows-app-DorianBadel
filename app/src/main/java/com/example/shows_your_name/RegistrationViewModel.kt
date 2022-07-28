package com.example.shows_your_name

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.databinding.FragmentRegisterFragmentBinding
import com.example.shows_your_name.models.RegisterRequest
import com.example.shows_your_name.models.RegisterResponse
import com.example.shows_your_name.newtworking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel: ViewModel(){

    private val ctEmail = "Email"

    private val registrationResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getRegistrationResultLiveData(): LiveData<Boolean> {
        return registrationResultLiveData
    }

    fun onRegisterButtonClicked(fragment: RegisterFragment,binding: FragmentRegisterFragmentBinding) {
        val registerRequest = RegisterRequest(
            email = binding.emailTexttxt.text.toString(),
            password = binding.passwordTexttxt.text.toString(),
            passwordConfirmation = binding.passwordRepeatTexttxt.text.toString()
        )
        ApiModule.retrofit.register(registerRequest)
            .enqueue(object: Callback<RegisterResponse>{
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    registrationResultLiveData.value = false
                }

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val bundle = bundleOf(ctEmail to binding.emailTexttxt.text.toString())

                    fragment.findNavController().navigate(R.id.reg_to_loginFraagment,bundle)

                    registrationResultLiveData.value = response.isSuccessful
                }
            })

    }
}