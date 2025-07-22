package com.practicum.playlistmaker.library.domain.models

import com.practicum.playlistmaker.search.track_search.domain.models.Track

data class PlaylistInfo(
    val id: Int,
    val coverPath: String?,
    val title: String,
    val description: String?,
    val duration: Int,
    val tracks: List<Track>
)
