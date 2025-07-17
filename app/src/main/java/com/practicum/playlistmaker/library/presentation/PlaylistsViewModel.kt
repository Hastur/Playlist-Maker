package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private var addPlaylistSingle = SingleLiveEvent<Boolean>()
    fun getAddPlaylistSingle(): SingleLiveEvent<Boolean> = addPlaylistSingle

    fun addPlaylist(name: String, description: String, coverPath: String) {
        viewModelScope.launch {
            addPlaylistSingle.value = false
            playlistsInteractor.addPlaylist(
                Playlist(
                    name = name,
                    description = description,
                    coverPath = coverPath,
                    tracksIds = listOf(),
                    tracksCount = 0
                )
            )
            addPlaylistSingle.value = true
        }
    }
}