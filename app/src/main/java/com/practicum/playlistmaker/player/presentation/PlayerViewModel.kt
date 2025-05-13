package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.models.PlayingStatus
import com.practicum.playlistmaker.search.track_search.domain.models.Track

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) :
    ViewModel() {

    companion object {
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.providePlayerInteractor()
                PlayerViewModel(track, interactor)
            }
        }
    }

    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData

    private var playingStatusLiveData = MutableLiveData<PlayingStatus>()
    fun getPlayingStatusLiveData(): LiveData<PlayingStatus> = playingStatusLiveData

    private val handler = Handler(Looper.getMainLooper())

    init {
        if (track.previewUrl != null) {
            playerInteractor.preparePlayer(
                source = track.previewUrl,
                onPrepared = {
                    screenStateLiveData.postValue(PlayerScreenState.Content(track))
                },
                onComplete = {
                    playingStatusLiveData.value =
                        getCurrentPlayingStatus().copy(playingTime = 0, isPlaying = false)
                }
            )
        } else screenStateLiveData.postValue(PlayerScreenState.Error)
    }

    fun playOrPause() {
        playerInteractor.controlPlayer(
            onStart = {
                playingStatusLiveData.value = getCurrentPlayingStatus().copy(isPlaying = true)
                handler.post(playingTimeTask)
            },
            onPause = {
                playingStatusLiveData.value = getCurrentPlayingStatus().copy(isPlaying = false)
                handler.removeCallbacks(playingTimeTask)
            }
        )
    }

    fun pause() {
        playerInteractor.pausePlayer(
            onPause = {
                playingStatusLiveData.value = getCurrentPlayingStatus().copy(isPlaying = false)
                handler.removeCallbacks(playingTimeTask)
            }
        )
    }

    private fun getCurrentPlayingStatus(): PlayingStatus =
        playingStatusLiveData.value ?: PlayingStatus(0, false)

    private val playingTimeTask = object : Runnable {
        override fun run() {
            playingStatusLiveData.value =
                getCurrentPlayingStatus().copy(playingTime = playerInteractor.getPlayingTime())
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onCleared() {
        playerInteractor.releasePlayer()
        handler.removeCallbacks(playingTimeTask)
    }
}