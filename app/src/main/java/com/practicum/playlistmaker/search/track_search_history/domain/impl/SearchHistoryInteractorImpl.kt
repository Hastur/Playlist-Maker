package com.practicum.playlistmaker.search.track_search_history.domain.impl

import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun getTracks(): List<Track> = repository.getTracks()

    override fun addTrack(track: Track): List<Track> {
        val trackList = repository.getTracks().toMutableList()
        if (trackList.any { it.trackId == track.trackId }) {
            trackList.remove(trackList.first { it.trackId == track.trackId })
        }
        if (trackList.size >= 10) trackList.removeAt(0)
        trackList.add(track)
        repository.saveTracks(trackList)
        return trackList
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}