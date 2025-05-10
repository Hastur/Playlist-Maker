package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.dto.PlayerStateDto
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.models.PlayerState

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var state = PlayerStateDto.STATE_DEFAULT

    override fun preparePlayer(source: String, onPrepared: () -> Unit, onComplete: () -> Unit) {
        mediaPlayer.setDataSource(source)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared()
            state = PlayerStateDto.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            onComplete()
            stopPlayer()
        }
    }

    override fun getState(): PlayerState {
        return when (state) {
            PlayerStateDto.STATE_DEFAULT -> PlayerState.STATE_DEFAULT
            PlayerStateDto.STATE_PREPARED -> PlayerState.STATE_PREPARED
            PlayerStateDto.STATE_PLAYING -> PlayerState.STATE_PLAYING
            PlayerStateDto.STATE_PAUSED -> PlayerState.STATE_PAUSED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        state = PlayerStateDto.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        state = PlayerStateDto.STATE_PAUSED
    }

    override fun stopPlayer() {
        mediaPlayer.pause()
        mediaPlayer.seekTo(0)
        state = PlayerStateDto.STATE_PREPARED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getPlayingTime(): Int = mediaPlayer.currentPosition
}