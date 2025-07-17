package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.domain.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val database: AppDatabase,
    private val converter: PlaylistDbConverter
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) =
        database.playlistDao().insertPlaylist(converter.mapPlaylistToEntity(playlist))

    override suspend fun editPlaylist(playlist: Playlist) =
        database.playlistDao().updatePlaylist(converter.mapPlaylistToEntity(playlist))

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = database.playlistDao()
            .getPlaylists()
            .map { playlist -> converter.mapEntityToPlaylist(playlist) }
        emit(playlists)
    }

}