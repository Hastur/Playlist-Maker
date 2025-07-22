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

    override suspend fun getPlaylistById(id: Int): Flow<Playlist> = repository.getPlaylistById(id)

    override suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>> =
        repository.getTracksByIds(ids)

    override suspend fun removeFromPlaylist(trackId: Int, playlistId: Int) =
        repository.removeFromPlaylist(trackId, playlistId)

    override suspend fun removePlaylist(playlistId: Int) = repository.removePlaylist(playlistId)

}