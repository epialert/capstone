package com.dicoding.capstoneprojek.ui.login

import androidx.lifecycle.ViewModel
import com.dicoding.capstoneprojek.data.repository.Repository

class ViewModelLogin (private val repository: Repository) : ViewModel() {
    fun login(account: String, password: String) = repository.login(account, password)
}