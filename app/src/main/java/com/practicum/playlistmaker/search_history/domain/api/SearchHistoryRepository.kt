package com.practicum.playlistmaker.search_history.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun getTracks(): List<Track>
    fun saveTracks(tracks: List<Track>)
    fun clearHistory()
}