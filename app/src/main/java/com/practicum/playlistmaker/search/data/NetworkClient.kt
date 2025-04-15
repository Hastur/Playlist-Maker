package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun makeRequest(dto: Any): Response
}