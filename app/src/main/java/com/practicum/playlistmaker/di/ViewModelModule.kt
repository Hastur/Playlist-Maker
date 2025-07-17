package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.presentation.FavoritesViewModel
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import com.practicum.playlistmaker.util.Utils
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { SearchViewModel(get(), get()) }

    viewModel { (serializedTrack: String) ->
        val track = Utils().createFromJson(serializedTrack, Track::class.java)
        PlayerViewModel(track, get(), get())
    }

    viewModel { SettingsViewModel(get(), get()) }

    viewModel { FavoritesViewModel(get()) }

    viewModel { PlaylistsViewModel(get()) }

}