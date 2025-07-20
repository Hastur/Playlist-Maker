package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addToPlaylist(track: Track, playlist: Playlist)

}