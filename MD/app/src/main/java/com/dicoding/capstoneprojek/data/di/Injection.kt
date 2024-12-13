package com.dicoding.capstoneprojek.data.di

import android.content.Context
import com.dicoding.capstoneprojek.data.preference.UserPref
import com.dicoding.capstoneprojek.data.preference.dataStore
import com.dicoding.capstoneprojek.data.repository.Repository
import com.dicoding.capstoneprojek.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(
        context: Context
    ): Repository {
        val pref = UserPref.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(pref, apiService)
    }
}