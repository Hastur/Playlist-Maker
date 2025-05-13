package com.practicum.playlistmaker.player.presentation.models

import com.practicum.playlistmaker.search.track_search.domain.models.Track

sealed class PlayerScreenState {
    data object Loading : PlayerScreenState()
    data object Error : PlayerScreenState()
    data class Content(val trackModel: Track) : PlayerScreenState()
}