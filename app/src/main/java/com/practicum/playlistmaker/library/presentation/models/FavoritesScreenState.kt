package com.practicum.playlistmaker.library.presentation.models

import com.practicum.playlistmaker.search.track_search.domain.models.Track

sealed class FavoritesScreenState {
    data object Loading : FavoritesScreenState()
    data object Empty : FavoritesScreenState()
    data class Content(val favoriteTracks: List<Track>) : FavoritesScreenState()
}