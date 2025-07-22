package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.PlaylistInfo
import com.practicum.playlistmaker.library.presentation.models.PlaylistItemScreenState
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class PlaylistsItemViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private var playlistWithTracksLiveData =
        MutableLiveData<PlaylistItemScreenState>(PlaylistItemScreenState.Loading)

    fun getPlaylistWithTracksLiveData(): LiveData<PlaylistItemScreenState> =
        playlistWithTracksLiveData

    private var toastSingleEvent = SingleLiveEvent<Int>()
    fun getToastSingleEvent(): SingleLiveEvent<Int> = toastSingleEvent

    private lateinit var currentPlaylist: PlaylistInfo

    private var tracksAmountPlural: String? = null
    fun setPlural(tracksAmount: String) {
        tracksAmountPlural = tracksAmount
    }

    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(id).collect { playlist ->
                playlistsInteractor.getTracksByIds(playlist.tracksIds).collect { tracks ->
                    currentPlaylist = PlaylistInfo(
                        id = playlist.id,
                        coverPath = playlist.coverPath,
                        title = playlist.name,
                        description = playlist.description,
                        duration = getPlaylistDuration(tracks),
                        tracks = tracks
                    )
                    playlistWithTracksLiveData.postValue(
                        PlaylistItemScreenState.Content(currentPlaylist)
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

    fun sharePlaylist() {
        if (currentPlaylist.tracks.isNotEmpty()) {
            val title = currentPlaylist.title
            val description =
                if (!currentPlaylist.description.isNullOrEmpty()) "${currentPlaylist.description}\n"
                else ""
            val tracksCount = tracksAmountPlural
            var trackList = ""
            currentPlaylist.tracks.forEachIndexed { index, current ->
                val number = index + 1
                trackList =
                    trackList.plus("$number ${current.artistName} - ${current.trackName} ${current.trackTime}\n")
            }
            val message = "$title\n$description$tracksCount\n$trackList"

            sharingInteractor.shareMessage(message)

        } else toastSingleEvent.postValue(R.string.playlist_share_no_tracks)
    }
}