package com.practicum.playlistmaker.search.track_search_history.data

import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl(private val localStorage: SearchHistoryStorage) :
    SearchHistoryRepository {

    override fun getTracks(): List<Track> = localStorage.getTracksFromPrefs()

    override fun saveTracks(tracks: List<Track>) {
        localStorage.saveTracksToPrefs(tracks)
    }

    override fun clearHistory() {
        localStorage.clearTracksPrefs()
    }
}