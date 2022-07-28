package com.example.shows_your_name.newtworking

import com.example.shows_your_name.models.*
import retrofit2.Call
import retrofit2.http.*

interface ShowsApiService {
 @POST("/users")
 fun register(@Body request: RegisterRequest): Call<RegisterResponse>

 @POST("/users/sign_in")
 fun login(@Body request: LoginRequest): Call<LoginResponse>

 @GET("/shows")
 fun getShows(@Header("access-token") accessToken: String,
              @Header("client") client: String,
              @Header("uid") uid: String,
              @Header("token-type") tokenType: String): Call<ShowsResponse>
}
