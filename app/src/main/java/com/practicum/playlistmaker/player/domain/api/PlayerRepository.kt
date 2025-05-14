package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface PlayerRepository {
    fun preparePlayer(source: String, onPrepared: () -> Unit, onComplete: () -> Unit)
    fun getState(): PlayerState
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun releasePlayer()
    fun getPlayingTime(): String
}