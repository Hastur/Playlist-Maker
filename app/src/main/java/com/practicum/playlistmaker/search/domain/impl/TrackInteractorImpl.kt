package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTrack(searchText: String, consumer: TrackInteractor.TrackConsumer) {
        Executors.newCachedThreadPool().execute {
            consumer.consume(repository.searchTrack(searchText))
        }
    }
}