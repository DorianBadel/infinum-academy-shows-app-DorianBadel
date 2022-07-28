package com.example.shows_your_name.newtworking

import com.example.shows_your_name.models.LoginRequest
import com.example.shows_your_name.models.LoginResponse
import com.example.shows_your_name.models.RegisterRequest
import com.example.shows_your_name.models.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ShowsApiService {
 @POST("/users")
 fun register(@Body request: RegisterRequest): Call<RegisterResponse>

 @POST("/users/sign_in")
 fun login(@Body request: LoginRequest): Call<LoginResponse>
}