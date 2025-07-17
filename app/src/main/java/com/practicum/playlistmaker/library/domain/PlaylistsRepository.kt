package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun editPlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

}