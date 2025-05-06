package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchRepository {
    fun searchTrack(searchText: String): List<Track>?
}