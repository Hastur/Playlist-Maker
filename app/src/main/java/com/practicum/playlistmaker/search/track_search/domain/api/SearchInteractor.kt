package com.practicum.playlistmaker.search.track_search.domain.api

import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTrack(searchText: String): Flow<Pair<List<Track>?, ErrorType?>>
}