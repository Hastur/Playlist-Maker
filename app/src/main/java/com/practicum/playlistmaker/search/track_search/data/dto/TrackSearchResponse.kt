package com.practicum.playlistmaker.search.track_search.data.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()