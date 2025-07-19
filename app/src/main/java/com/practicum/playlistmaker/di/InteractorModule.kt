package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.library.domain.impl.PlaylistsInteractorImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.track_search_history.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<SearchInteractor> { SearchInteractorImpl(get()) }

    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }

    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }

    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    single<SharingInteractor> { SharingInteractorImpl(get()) }

    single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }

    single<PlaylistsInteractor> { PlaylistsInteractorImpl(get()) }

}