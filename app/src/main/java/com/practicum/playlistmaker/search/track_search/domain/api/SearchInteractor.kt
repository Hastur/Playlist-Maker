package com.practicum.playlistmaker.search.track_search.domain.api

import com.practicum.playlistmaker.search.track_search.data.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track

interface SearchInteractor {
    fun searchTrack(searchText: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?, errorType: ErrorType?)
    }
}