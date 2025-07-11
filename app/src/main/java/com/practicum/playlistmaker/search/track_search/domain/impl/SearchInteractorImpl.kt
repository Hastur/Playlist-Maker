package com.practicum.playlistmaker.search.track_search.domain.impl

import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun searchTrack(searchText: String): Flow<Pair<List<Track>?, ErrorType?>> =
        repository.searchTrack(searchText).map { resource ->
            when (resource) {
                is Resource.Success -> Pair(resource.data, null)
                is Resource.Error -> Pair(null, resource.errorType)
            }
        }
}