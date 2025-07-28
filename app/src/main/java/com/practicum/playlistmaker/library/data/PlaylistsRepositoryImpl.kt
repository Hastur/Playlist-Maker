package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.domain.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class PlaylistsRepositoryImpl(
    private val database: AppDatabase,
    private val converter: PlaylistDbConverter
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) =
        database.playlistDao().insertPlaylist(converter.mapPlaylistToEntity(playlist))

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        emit(getAllPlaylists().asReversed())
    }

    override suspend fun addToPlaylist(track: Track, playlist: Playlist) {
        database.trackToPlaylistDao()
            .insertTrackToPlaylist(converter.mapTrackToPlaylistEntity(track))

        val newIdsList = playlist.tracksIds.toMutableList()
        newIdsList.add(track.trackId)
        val modifiedPlaylistEntity =
            converter.mapPlaylistToEntity(playlist.copy(tracksIds = newIdsList))
        database.playlistDao().updatePlaylist(modifiedPlaylistEntity)
    }

    override suspend fun getPlaylistById(id: Int): Flow<Playlist> = flow {
        emit(getPlaylist(id))
    }

    override suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>> = flow {
        val tracks = database.trackToPlaylistDao()
            .getTracksByIds(ids)
            .map { trackEntity -> converter.mapPlaylistEntityToTrack(trackEntity) }
        emit(tracks)
    }

    override suspend fun removeFromPlaylist(trackId: Int, playlistId: Int) {
        val playlist = getPlaylist(playlistId)
        val newIdsList = playlist.tracksIds.toMutableList()
        newIdsList.remove(trackId)
        val modifiedPlaylistEntity =
            converter.mapPlaylistToEntity(playlist.copy(tracksIds = newIdsList))
        database.playlistDao().updatePlaylist(modifiedPlaylistEntity)

        val playlists = getAllPlaylists()
        removeHomelessTrack(playlists, trackId)
    }

    override suspend fun removePlaylist(playlistId: Int) {
        val playlistToRemove = getPlaylist(playlistId)
        runBlocking {
            database.playlistDao().deletePlaylist(converter.mapPlaylistToEntity(playlistToRemove))

            val playlists = getAllPlaylists()
            playlistToRemove.tracksIds.forEach { currentId ->
                removeHomelessTrack(playlists, currentId)
            }
        }
    }

    override suspend fun editPlaylist(
        playlistId: Int,
        title: String,
        description: String,
        coverPath: String
    ) {
        val playlist = getPlaylist(playlistId)
        val updatedPlaylist =
            playlist.copy(name = title, description = description, coverPath = coverPath)
        database.playlistDao().updatePlaylist(converter.mapPlaylistToEntity(updatedPlaylist))
    }

    private suspend fun getAllPlaylists(): List<Playlist> =
        database.playlistDao()
            .getPlaylists()
            .map { playlist -> converter.mapEntityToPlaylist(playlist) }

    private suspend fun getPlaylist(id: Int): Playlist =
        converter.mapEntityToPlaylist(database.playlistDao().getPlaylistById(id))

    private suspend fun removeHomelessTrack(playlists: List<Playlist>, trackId: Int) {
        val anyPlaylistHasTrack = playlists.any { it.tracksIds.contains(trackId) }
        if (!anyPlaylistHasTrack) database.trackToPlaylistDao().deleteHomelessTrack(trackId)
    }
}