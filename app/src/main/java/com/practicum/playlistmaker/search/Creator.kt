package com.practicum.playlistmaker.search

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository = TrackRepositoryImpl(RetrofitNetworkClient())

    fun provideTrackInteractor(): TrackInteractor = TrackInteractorImpl(getTrackRepository())

    private fun getPlayerRepository(): PlayerRepository = PlayerRepositoryImpl(MediaPlayer())

    fun providePlayerInteractor(): PlayerInteractor = PlayerInteractorImpl(getPlayerRepository())
}