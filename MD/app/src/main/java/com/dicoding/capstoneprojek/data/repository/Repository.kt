package com.dicoding.capstoneprojek.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.capstoneprojek.data.preference.UserModel
import com.dicoding.capstoneprojek.data.preference.UserPref
import com.dicoding.capstoneprojek.data.response.ErrorResponse
import com.dicoding.capstoneprojek.data.response.RegisterResponse
import com.dicoding.capstoneprojek.data.result.ResultCode
import com.dicoding.capstoneprojek.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class Repository private constructor(
    private val userPref: UserPref,
    private val apiService: ApiService
) {
    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    suspend fun logout() {
        userPref.logout()
    }

    fun register(
        username: String,
        name: String,
        email: String,
        password: String,
    ): LiveData<ResultCode<RegisterResponse>> = liveData {
        emit(ResultCode.Loading)
        try {
            val response = apiService.register(username = username,name = name, email = email, password = password)
            emit(ResultCode.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultCode.Error(errorMessage.toString()))
        }
    }
    fun login(account: String, password: String): LiveData<ResultCode<UserModel>> = liveData {
        emit(ResultCode.Loading)

        try {
            // Panggil API dan dapatkan response
            val response = apiService.login(account, password)

            // Ambil token dari LoginResponse, bukan dari user
            val token = response.token

            // Buat UserModel dengan account dan token
            val userModel = UserModel(account, token, true)

            // Simpan sesi pengguna
            userPref.saveSession(userModel)

            // Emit hasil sukses
            emit(ResultCode.Success(userModel))
        } catch (e: HttpException) {
            // Parsing error dari errorBody
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message

            // Emit hasil error
            emit(ResultCode.Error(errorMessage.toString()))
        }

        // Ambil data sesi dari userPref
        val data: LiveData<ResultCode<UserModel>> = userPref.getSession().asLiveData().map {
            ResultCode.Success(it)
        }
        emitSource(data)
    }


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPref: UserPref,
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPref, apiService)
            }.also { instance = it }
    }

}