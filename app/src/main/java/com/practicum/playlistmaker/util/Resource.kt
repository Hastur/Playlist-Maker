package com.practicum.playlistmaker.util

import com.practicum.playlistmaker.search.track_search.data.ErrorType

sealed class Resource<T>(val data: T? = null, val errorType: ErrorType? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: ErrorType, data: T? = null) : Resource<T>(data, message)
}