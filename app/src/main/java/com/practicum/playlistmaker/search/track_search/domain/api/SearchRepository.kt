package com.practicum.playlistmaker.search.track_search.domain.api

import com.practicum.playlistmaker.search.track_search.domain.models.Track

interface SearchRepository {
    fun searchTrack(searchText: String): List<Track>?
}