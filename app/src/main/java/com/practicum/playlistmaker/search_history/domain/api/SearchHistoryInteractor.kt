package com.practicum.playlistmaker.search_history.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun getTracks(): List<Track>
    fun addTrack(track: Track): List<Track>
    fun clearHistory()
}