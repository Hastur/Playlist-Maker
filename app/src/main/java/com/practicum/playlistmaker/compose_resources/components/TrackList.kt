package com.practicum.playlistmaker.compose_resources.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.practicum.playlistmaker.search.track_search.domain.models.Track

@Composable
fun TrackList(tracks: List<Track>, clickListener: (Track) -> Unit) {
    LazyColumn {
        items(tracks) { track ->
            TrackItem(track, clickListener)
        }
    }
}