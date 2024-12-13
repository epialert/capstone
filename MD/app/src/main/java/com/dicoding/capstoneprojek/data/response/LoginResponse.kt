package com.dicoding.capstoneprojek.data.response

import com.google.gson.annotations.SerializedName


data class LoginResponse(

    @field:SerializedName("status")
    val status: Boolean, // Sesuai dengan key "status" dari JSON

    @field:SerializedName("message")
    val message: String, // Key "message" dari JSON

    @field:SerializedName("token")
    val token: String, // Key "token" dari JSON

    @field:SerializedName("user")
    val user: User // Key "user" yang merupakan objek
)

data class User(

    @field:SerializedName("username")
    val username: String, // Key "username" dari JSON

    @field:SerializedName("name")
    val name: String, // Key "name" dari JSON

    @field:SerializedName("email")
    val email: String, // Key "email" dari JSON


)



