package com.practicum.playlistmaker.search.track_search.data

import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.track_search.domain.models.Track

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTrack(searchText: String): List<Track>? {
        val response = networkClient.makeRequest(TrackSearchRequest(searchText))
        return when (response.resultCode) {
            200 -> {
                (response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
            }

            -1 -> null

            else -> emptyList()
        }
    }
}