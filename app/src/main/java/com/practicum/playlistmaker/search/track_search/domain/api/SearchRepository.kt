package com.practicum.playlistmaker.search.track_search.domain.api

import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTrack(searchText: String): Flow<Resource<List<Track>>>
}