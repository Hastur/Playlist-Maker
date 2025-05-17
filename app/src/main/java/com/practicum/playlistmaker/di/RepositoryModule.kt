package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.track_search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.track_search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.track_search_history.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> { SearchRepositoryImpl(get()) }

    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }

    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }

}