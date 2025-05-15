package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.track_search.domain.models.Track

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) :
    ViewModel() {

    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData

    private val handler = Handler(Looper.getMainLooper())

    init {
        if (track.previewUrl != null) {
            playerInteractor.preparePlayer(
                source = track.previewUrl,
                onPrepared = {
                    screenStateLiveData.postValue(PlayerScreenState.Prepared(track))
                },
                onComplete = {
                    screenStateLiveData.postValue(
                        PlayerScreenState.Playing("00:00", false)
                    )
                }
            )
        } else screenStateLiveData.postValue(PlayerScreenState.Error)
    }

    fun playOrPause() {
        playerInteractor.controlPlayer(
            onStart = {
                screenStateLiveData.postValue(
                    PlayerScreenState.Playing(playerInteractor.getPlayingTime(), true)
                )
                handler.post(playingTimeTask)
            },
            onPause = {
                screenStateLiveData.postValue(
                    PlayerScreenState.Playing(playerInteractor.getPlayingTime(), false)
                )
                handler.removeCallbacks(playingTimeTask)
            }
        )
    }

    fun pause() {
        playerInteractor.pausePlayer(
            onPause = {
                screenStateLiveData.postValue(
                    PlayerScreenState.Playing(playerInteractor.getPlayingTime(), false)
                )
                handler.removeCallbacks(playingTimeTask)
            }
        )
    }

    private val playingTimeTask = object : Runnable {
        override fun run() {
            screenStateLiveData.postValue(
                PlayerScreenState.Playing(playerInteractor.getPlayingTime(), true)
            )
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onCleared() {
        playerInteractor.releasePlayer()
        handler.removeCallbacks(playingTimeTask)
    }
}