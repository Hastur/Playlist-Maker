package com.practicum.playlistmaker.search.track_search.domain.impl

import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun searchTrack(searchText: String): Resource<List<Track>> {
        lateinit var resource: Resource<List<Track>>
        val executor = Executors.newCachedThreadPool()
        executor.execute { resource = repository.searchTrack(searchText) }
        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        return resource
    }
}