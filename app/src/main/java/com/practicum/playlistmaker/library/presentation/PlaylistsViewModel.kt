package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.models.PlaylistInfo
import com.practicum.playlistmaker.library.presentation.models.PlaylistsScreenState
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private var playlistChangedSingle = SingleLiveEvent<Boolean>()
    fun getPlaylistChangedSingle(): SingleLiveEvent<Boolean> = playlistChangedSingle

    private var playlistsLiveData =
        MutableLiveData<PlaylistsScreenState>(PlaylistsScreenState.Loading)

    fun getPlaylistsStateLiveData(): LiveData<PlaylistsScreenState> = playlistsLiveData

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
                if (result.isNotEmpty()) playlistsLiveData.postValue(
                    PlaylistsScreenState.Content(result)
                )
                else playlistsLiveData.postValue(PlaylistsScreenState.Empty)
            }
        }
    }
}