package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converters.TrackDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.domain.FavoritesRepository
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesRepositoryImpl(private val database: AppDatabase, private val converter: TrackDbConverter): FavoritesRepository {

    override fun addToFavorites(track: Track) {
        TODO("Not yet implemented")
    }

    override fun removeFromFavorites(track: Track) {
        TODO("Not yet implemented")
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

}