package com.practicum.playlistmaker.search

import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository = TrackRepositoryImpl(RetrofitNetworkClient())

    fun provideTrackInteractor(): TrackInteractor = TrackInteractorImpl(getTrackRepository())
}