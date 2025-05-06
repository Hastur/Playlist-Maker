package com.practicum.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.search_history.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search_history.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search_history.domain.impl.SearchHistoryInteractorImpl

object Creator {
    private fun getSearchRepository(context: Context): SearchRepository =
        SearchRepositoryImpl(RetrofitNetworkClient(context))

    fun provideSearchInteractor(context: Context): SearchInteractor =
        SearchInteractorImpl(getSearchRepository(context))

    private fun getPlayerRepository(): PlayerRepository = PlayerRepositoryImpl(MediaPlayer())

    fun providePlayerInteractor(): PlayerInteractor = PlayerInteractorImpl(getPlayerRepository())

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(context)

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
}