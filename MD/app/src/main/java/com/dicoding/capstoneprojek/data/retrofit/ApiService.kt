package com.dicoding.capstoneprojek.data.retrofit

import com.dicoding.capstoneprojek.data.response.LoginResponse
import com.dicoding.capstoneprojek.data.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("account") account: String, // Bisa berupa username atau email
        @Field("password") password: String
    ): LoginResponse

}