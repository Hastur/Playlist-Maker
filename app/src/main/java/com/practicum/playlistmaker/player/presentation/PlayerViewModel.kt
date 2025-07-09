package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) :
    ViewModel() {

    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData

    private var timerJob: Job? = null

    init {
        if (track.previewUrl != null) {
            playerInteractor.preparePlayer(
                source = track.previewUrl,
                onPrepared = {
                    screenStateLiveData.postValue(PlayerScreenState.Prepared(track))
                },
                onComplete = {
                    timerJob?.cancel()
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
                startTimer()
            },
            onPause = {
                screenStateLiveData.postValue(
                    PlayerScreenState.Playing(playerInteractor.getPlayingTime(), false)
                )
                timerJob?.cancel()
            }
        )
    }

    fun pause() {
        playerInteractor.pausePlayer(
            onPause = {
                screenStateLiveData.postValue(
                    PlayerScreenState.Playing(playerInteractor.getPlayingTime(), false)
                )
                timerJob?.cancel()
            }
        )
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(300L)
                screenStateLiveData.postValue(
                    PlayerScreenState.Playing(playerInteractor.getPlayingTime(), true)
                )
            }
        }
    }

    override fun onCleared() {
        playerInteractor.releasePlayer()
    }
}