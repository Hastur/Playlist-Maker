package com.practicum.playlistmaker.search.track_search.presentation.models

import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track

sealed class SearchScreenState {
    data object Loading : SearchScreenState()
    data class Error(val errorType: ErrorType) : SearchScreenState()
    data class Content(val trackList: List<Track>) : SearchScreenState()
}