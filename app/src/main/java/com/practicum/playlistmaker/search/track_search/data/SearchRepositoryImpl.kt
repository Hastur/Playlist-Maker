package com.practicum.playlistmaker.search.track_search.data

import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.util.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTrack(searchText: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.makeRequest(TrackSearchRequest(searchText))
        when (response.resultCode) {
            200 -> {
                val searchResponse = response as TrackSearchResponse
                if (searchResponse.results.isNotEmpty()) {
                    val data = searchResponse.results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            Utils().formatTimeAsString(it.trackTimeMillis),
                            it.artworkUrl100,
                            it.collectionName,
                            Utils().formatYearAsString(it.releaseDate),
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Resource.Success(data))
                } else emit(Resource.Error(ErrorType.NothingFound))
            }

            -1 -> emit(Resource.Error(ErrorType.NoInternet))

            else -> emit(Resource.Error(ErrorType.ServerError))
        }
    }
}