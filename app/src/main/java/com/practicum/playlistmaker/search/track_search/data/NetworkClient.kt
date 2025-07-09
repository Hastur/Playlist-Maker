package com.practicum.playlistmaker.search.track_search.data

import com.practicum.playlistmaker.search.track_search.data.dto.Response

interface NetworkClient {
    suspend fun makeRequest(dto: Any): Response
}