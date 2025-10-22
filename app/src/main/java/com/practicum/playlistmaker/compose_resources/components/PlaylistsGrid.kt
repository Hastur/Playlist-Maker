package com.practicum.playlistmaker.compose_resources.components

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.practicum.playlistmaker.library.domain.models.Playlist

@Composable
fun PlaylistsGrid(playlists: List<Playlist>, clickListener: (Playlist) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(playlists) { playlist ->
            PlaylistItem(playlist, clickListener)
        }
    }
}