package com.practicum.playlistmaker

import android.app.Application
import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.track_search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.track_search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.track_search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.search.track_search_history.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.track_search_history.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl

object Creator {
    private lateinit var application: Application

    fun setApplication(app: Application) {
        application = app
    }

    private fun getSearchRepository(): SearchRepository =
        SearchRepositoryImpl(RetrofitNetworkClient(application))

    fun provideSearchInteractor(): SearchInteractor =
        SearchInteractorImpl(getSearchRepository())

    private fun getPlayerRepository(): PlayerRepository = PlayerRepositoryImpl(MediaPlayer())

    fun providePlayerInteractor(): PlayerInteractor = PlayerInteractorImpl(getPlayerRepository())

    private fun getSearchHistoryRepository(): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(application)

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository())

    private fun getSettingsRepository(): SettingsRepository = SettingsRepositoryImpl(application)

    fun provideSettingsInteractor(): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository())
}