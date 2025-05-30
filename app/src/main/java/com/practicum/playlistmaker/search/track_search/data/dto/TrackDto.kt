package com.practicum.playlistmaker.search.track_search.data.dto

import java.util.Date

data class TrackDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: Date?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
)
