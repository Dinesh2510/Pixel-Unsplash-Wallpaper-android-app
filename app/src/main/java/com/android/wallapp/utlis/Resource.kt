package com.android.wallapp.utlis

sealed class Resource<T>(val data: T?, val msg: String?) {
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T>(message: String?) : Resource<T>(null, message)
}