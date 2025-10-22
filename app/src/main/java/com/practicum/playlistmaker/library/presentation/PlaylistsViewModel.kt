package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.models.PlaylistInfo
import com.practicum.playlistmaker.library.presentation.models.PlaylistsScreenState
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val _screenStateFLow =
        MutableStateFlow<PlaylistsScreenState>(PlaylistsScreenState.Loading)

    fun getScreenStateFlow(): StateFlow<PlaylistsScreenState> = _screenStateFLow.asStateFlow()

    private var playlistChangedSingle = SingleLiveEvent<Boolean>()
    fun getPlaylistChangedSingle(): SingleLiveEvent<Boolean> = playlistChangedSingle

    private var buttonAddClickedSingle = SingleLiveEvent<Boolean>()
    fun getButtonAddClickedSingle(): SingleLiveEvent<Boolean> = buttonAddClickedSingle

    private var playlistClickedSingle = SingleLiveEvent<Playlist>()
    fun getPlaylistClickedSingle(): SingleLiveEvent<Playlist> = playlistClickedSingle

    fun addPlaylist(name: String, description: String, coverPath: String) {
        viewModelScope.launch {
            playlistChangedSingle.value = false
            playlistsInteractor.addPlaylist(
                Playlist(
                    name = name,
                    description = description,
                    coverPath = coverPath,
                    tracksIds = listOf()
                )
            )
            playlistChangedSingle.value = true
        }
    }

    fun editPlaylist(
        name: String,
        description: String,
        coverPath: String,
        playlistInfo: PlaylistInfo?
    ) {
        viewModelScope.launch {
            playlistChangedSingle.value = false
            if (!(name == playlistInfo?.title
                        && description == playlistInfo.description
                        && coverPath == playlistInfo.coverPath
                        )
            ) {
                playlistInfo?.id?.let {
                    playlistsInteractor.editPlaylist(
                        playlistId = it,
                        title = name,
                        description = description,
                        coverPath = coverPath
                    )
                }
            }
            playlistChangedSingle.value = true
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { result ->
                if (result.isNotEmpty()) _screenStateFLow.update {
                    PlaylistsScreenState.Content(result)
                }
                else _screenStateFLow.update { PlaylistsScreenState.Empty }
            }
        }
    }

    fun onButtonAddClicked() {
        buttonAddClickedSingle.value = true
    }

    fun onPlaylistClicked(playlist: Playlist) {
        playlistClickedSingle.value = playlist
    }
}