package com.practicum.playlistmaker.search.track_search_history.data

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val localStorage: SearchHistoryStorage,
    private val database: AppDatabase
) :
    SearchHistoryRepository {

    override suspend fun getTracks(): List<Track> {
        val tracks = localStorage.getTracksFromPrefs()
        val favoriteTracksIds = database.trackDao().getTracksIds()
        tracks.forEach { current ->
            if (current.trackId in favoriteTracksIds) current.isFavorite = true
        }
        return tracks
    }

    override fun saveTracks(tracks: List<Track>) {
        localStorage.saveTracksToPrefs(tracks)
    }

    override fun clearHistory() {
        localStorage.clearTracksPrefs()
    }
}