package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.models.PlayerState

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(source: String, onPrepared: () -> Unit, onComplete: () -> Unit) {
        repository.preparePlayer(source, onPrepared, onComplete)
    }

    override fun controlPlayer(onStart: () -> Unit, onPause: () -> Unit) {
        val state = repository.getState()
        when (state) {
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                repository.startPlayer()
                onStart()
            }

            PlayerState.STATE_PLAYING -> {
                repository.pausePlayer()
                onPause()
            }

            else -> {}
        }
    }

    override fun startPlayer(onStart: () -> Unit) {
        repository.startPlayer()
        onStart()
    }

    override fun pausePlayer(onPause: () -> Unit) {
        repository.pausePlayer()
        onPause()
    }

    override fun resetPlayer() {
        repository.resetPlayer()
    }
}