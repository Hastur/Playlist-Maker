package com.practicum.playlistmaker.player.services

import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import kotlinx.coroutines.flow.StateFlow

interface PlayerControl {
    fun startPlayer()
    fun pausePlayer()
    fun getPlayerState(): StateFlow<PlayerScreenState>
    fun startForeground()
    fun stopForeground()
}