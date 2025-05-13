package com.practicum.playlistmaker.search.track_search.data

import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTrack(searchText: String): Resource<List<Track>> {
        val response = networkClient.makeRequest(TrackSearchRequest(searchText))
        return when (response.resultCode) {
            200 -> {
                val searchResponse = response as TrackSearchResponse
                if (searchResponse.results.isNotEmpty()) {
                    Resource.Success(searchResponse.results.map {
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
                    })
                } else Resource.Error(ErrorType.NothingFound)
            }

            -1 -> Resource.Error(ErrorType.NoInternet)

            else -> Resource.Error(ErrorType.ServerError)
        }
    }
}