package com.practicum.playlistmaker.search.track_search_history.data

import com.practicum.playlistmaker.search.track_search.domain.models.Track

interface SearchHistoryStorage {
    fun getTracksFromPrefs(): List<Track>
    fun saveTracksToPrefs(tracks: List<Track>)
    fun clearTracksPrefs()
}