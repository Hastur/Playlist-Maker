package com.practicum.playlistmaker.search.track_search.domain.models

import com.practicum.playlistmaker.R

enum class ErrorType(val imageId: Int, val messageId: Int) {
    NothingFound(R.drawable.ic_nothing_found, R.string.search_nothing_found),
    NoInternet(R.drawable.ic_no_internet, R.string.search_no_internet),
    ServerError(R.drawable.ic_nothing_found, R.string.server_error)
}