package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.FavoritesRepository
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) = repository.addToFavorites(track)

    override suspend fun removeFromFavorites(track: Track) = repository.removeFromFavorites(track)

    override fun getFavoriteTracks(): Flow<List<Track>> = repository.getFavoriteTracks()

}