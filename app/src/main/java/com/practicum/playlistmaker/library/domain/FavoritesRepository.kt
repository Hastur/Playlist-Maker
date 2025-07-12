package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun addToFavorites(track: Track)

    fun removeFromFavorites(track: Track)

    fun getFavoriteTracks(): Flow<List<Track>>

}