package com.dicoding.capstoneprojek.data.result

sealed class ResultCode<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultCode<T>()
    data class Error(val error: String) : ResultCode<Nothing>()
    data object Loading : ResultCode<Nothing>()
}