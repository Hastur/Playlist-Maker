package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(source: String, onPrepared: () -> Unit, onComplete: () -> Unit)
    fun controlPlayer(onStart: () -> Unit, onPause: () -> Unit)
    fun startPlayer(onStart: () -> Unit)
    fun pausePlayer(onPause: () -> Unit)
    fun resetPlayer()
}