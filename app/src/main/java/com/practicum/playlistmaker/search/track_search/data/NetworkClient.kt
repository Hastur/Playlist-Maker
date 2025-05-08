package com.practicum.playlistmaker.search.track_search.data

import com.practicum.playlistmaker.search.track_search.data.dto.Response

interface NetworkClient {
    fun makeRequest(dto: Any): Response
}