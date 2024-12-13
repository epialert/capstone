package com.dicoding.capstoneprojek.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user")
    val user: RegisteredUser
)


data class RegisteredUser(

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

)




