package com.practicum.playlistmaker.search.track_search.domain.impl

import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun searchTrack(searchText: String, consumer: SearchInteractor.TrackConsumer) {
        Executors.newCachedThreadPool().execute {
            when (val resource = repository.searchTrack(searchText)) {
                is Resource.Success -> consumer.consume(resource.data, null)
                is Resource.Error -> consumer.consume(null, resource.errorType)
            }
        }
    }
}