package com.wen.android.mtabuscomparison.common


sealed class Result<out R> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val msg: String) : Result<Nothing>()
}