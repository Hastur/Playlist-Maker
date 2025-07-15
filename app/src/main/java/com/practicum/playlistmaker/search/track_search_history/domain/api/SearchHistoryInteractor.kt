package com.practicum.playlistmaker.search.track_search_history.domain.api

import com.practicum.playlistmaker.search.track_search.domain.models.Track

interface SearchHistoryInteractor {
    suspend fun getTracks(): List<Track>
    suspend fun addTrack(track: Track): List<Track>
    fun clearHistory()
}