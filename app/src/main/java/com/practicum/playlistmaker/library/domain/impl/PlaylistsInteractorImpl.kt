package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist) = repository.addPlaylist(playlist)

    override suspend fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override suspend fun addToPlaylist(track: Track, playlist: Playlist) =
        repository.addToPlaylist(track, playlist)

}