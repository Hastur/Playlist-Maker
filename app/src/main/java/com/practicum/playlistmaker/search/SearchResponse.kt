package com.practicum.playlistmaker.search

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)