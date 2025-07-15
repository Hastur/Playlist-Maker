package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun addToFavorites(track: Track)

    suspend fun removeFromFavorites(track: Track)

    fun getFavoriteTracks(): Flow<List<Track>>

}