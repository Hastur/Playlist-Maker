package com.practicum.playlistmaker.library.presentation.models

import com.practicum.playlistmaker.library.domain.models.PlaylistInfo

sealed interface PlaylistItemScreenState {
    data object Loading : PlaylistItemScreenState
    data class Content(val playlistInfo: PlaylistInfo) : PlaylistItemScreenState
}