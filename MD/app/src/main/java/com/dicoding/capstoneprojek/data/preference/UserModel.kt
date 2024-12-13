package com.dicoding.capstoneprojek.data.preference

data class UserModel(
    val account: String,
    val token: String, // Tambahkan token
    val isLoggedIn: Boolean
)
