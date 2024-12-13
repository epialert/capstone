package com.dicoding.capstoneprojek.ui.register

import androidx.lifecycle.ViewModel
import com.dicoding.capstoneprojek.data.repository.Repository

class ViewModelRegister(
    private val repository: Repository
) : ViewModel() {
    fun register(
        username: String,
        name: String,
        email: String,
        password: String
    ) = repository.register(username,name, email, password)

}