package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converters.TrackDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.domain.FavoritesRepository
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val database: AppDatabase, private val converter: TrackDbConverter
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) =
        database.trackDao().insertTrack(converter.mapTrackToEntity(track))

    override suspend fun removeFromFavorites(track: Track) =
        database.trackDao().deleteTrack(converter.mapTrackToEntity(track))

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = database.trackDao()
            .getTracks()
            .map { track -> converter.mapEntityToTrack(track) }
            .asReversed()
        emit(tracks)
    }

}