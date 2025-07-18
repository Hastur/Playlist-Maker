package com.practicum.playlistmaker.library.presentation.models

import com.practicum.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistsScreenState {
    data object Loading : PlaylistsScreenState
    data object Empty : PlaylistsScreenState
    data class Content(val playlists: List<Playlist>) : PlaylistsScreenState
}