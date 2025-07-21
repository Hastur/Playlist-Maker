package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.PlaylistInfo
import com.practicum.playlistmaker.library.presentation.models.PlaylistItemScreenState
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class PlaylistsItemViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private var playlistWithTracksLiveData =
        MutableLiveData<PlaylistItemScreenState>(PlaylistItemScreenState.Loading)

    fun getPlaylistWithTracksLiveData(): LiveData<PlaylistItemScreenState> =
        playlistWithTracksLiveData

    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(id).collect { playlist ->
                playlistsInteractor.getTracksByIds(playlist.tracksIds).collect { tracks ->
                    playlistWithTracksLiveData.postValue(
                        PlaylistItemScreenState.Content(
                            PlaylistInfo(
                                id = playlist.id,
                                coverPath = playlist.coverPath,
                                title = playlist.name,
                                description = playlist.description,
                                duration = getPlaylistDuration(tracks),
                                tracks = tracks
                            )
                        )
                    )
                }
            }
        }
    }

    private fun getPlaylistDuration(tracks: List<Track>): Int {
        var durationMinutes = 0
        var durationSeconds = 0
        tracks.forEach { current ->
            val minAndSec = current.trackTime.split(":")
            durationMinutes = durationMinutes.plus(minAndSec.first().toInt())
            durationSeconds = durationSeconds.plus(minAndSec.last().toInt())
        }
        return durationMinutes.plus((durationSeconds / 60.0).roundToInt())
    }

    fun removeTrackFromPlaylist(trackId: Int, playlistId: Int) {
        viewModelScope.launch {
            runBlocking {
                playlistsInteractor.removeFromPlaylist(trackId, playlistId)
                getPlaylistById(playlistId)
            }
        }
    }
}