package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.player.services.PlayerControl
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData

    private var favoriteStateSingleEvent = SingleLiveEvent<Boolean>()
    fun getFavoriteStateSingleEvent(): SingleLiveEvent<Boolean> = favoriteStateSingleEvent

    private var playlistsSingleEvent = SingleLiveEvent<List<Playlist>>()
    fun getPlaylistsSingleEvent(): SingleLiveEvent<List<Playlist>> = playlistsSingleEvent

    private var addingResultSingleEvent = SingleLiveEvent<Boolean>()
    fun getAddingResultSingleEvent(): SingleLiveEvent<Boolean> = addingResultSingleEvent

    private var playerControl: PlayerControl? = null

    fun setPlayerControl(audioPlayerControl: PlayerControl) {
        playerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getPlayerState().collect {
                screenStateLiveData.postValue(it)
            }
        }
    }

    fun playOrPause() {
        when (screenStateLiveData.value) {
            is PlayerScreenState.Prepared -> {
                playerControl?.startPlayer()
            }

            is PlayerScreenState.Playing -> {
                if ((screenStateLiveData.value as PlayerScreenState.Playing).isPlaying) playerControl?.pausePlayer()
                else playerControl?.startPlayer()
            }

            else -> {}
        }
    }

    fun showServiceNotification() {
        if (screenStateLiveData.value is PlayerScreenState.Playing) {
            if ((screenStateLiveData.value as PlayerScreenState.Playing).isPlaying) playerControl?.startForeground()
        }
    }

    fun hideServiceNotification() {
        playerControl?.stopForeground()
    }

    fun removeAudioPlayerControl() {
        playerControl = null
    }

    fun onFavoriteClick() {
        viewModelScope.launch {
            track.isFavorite = !track.isFavorite
            if (track.isFavorite) favoritesInteractor.addToFavorites(track)
            else favoritesInteractor.removeFromFavorites(track)
            favoriteStateSingleEvent.value = track.isFavorite
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { result ->
                playlistsSingleEvent.value = result
            }
        }
    }

    fun addToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (track.trackId !in playlist.tracksIds) {
                playlistsInteractor.addToPlaylist(track, playlist)
                addingResultSingleEvent.value = true
            } else addingResultSingleEvent.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerControl = null
    }
}