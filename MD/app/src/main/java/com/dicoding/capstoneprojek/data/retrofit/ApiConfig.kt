package com.dicoding.capstoneprojek.data.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        // Mengembalikan instance ApiService
        fun getApiService(): ApiService {
            // Logging untuk memonitor request dan response
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            // Mengonfigurasi OkHttpClient dengan timeout
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Menambahkan logging interceptor
                .connectTimeout(30, TimeUnit.SECONDS)  // Set timeout koneksi
                .readTimeout(30, TimeUnit.SECONDS)     // Set timeout membaca response
                .writeTimeout(30, TimeUnit.SECONDS)    // Set timeout menulis request
                .build()

            // Membuat instance Retrofit dengan client dan base URL
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.epialert.my.id/") // URL API Base
                .addConverterFactory(GsonConverterFactory.create()) // Converter JSON ke objek
                .client(client) // Menambahkan client ke Retrofit
                .build()

            // Mengembalikan ApiService untuk akses endpoint API
            return retrofit.create(ApiService::class.java)
        }
    }
}
