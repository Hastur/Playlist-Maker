package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun searchTrack(searchText: String, consumer: SearchInteractor.TrackConsumer) {
        Executors.newCachedThreadPool().execute {
            consumer.consume(repository.searchTrack(searchText))
        }
    }
}