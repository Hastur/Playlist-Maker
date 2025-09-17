package com.practicum.playlistmaker.search.track_search.data.network

import android.content.Context
import com.practicum.playlistmaker.search.track_search.data.NetworkClient
import com.practicum.playlistmaker.search.track_search.data.dto.Response
import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val searchService: TrackSearchApi,
    private val context: Context
) : NetworkClient {
    override suspend fun makeRequest(dto: Any): Response {
        return if (Utils().isNetworkAvailable(context)) {
            if (dto is TrackSearchRequest) {
                withContext(Dispatchers.IO) {
                    try {
                        val response = searchService.searchTrack(dto.searchText)
                        response.apply { resultCode = 200 }
                    } catch (e: Throwable) {
                        Response().apply { resultCode = 500 }
                    }
                }
            } else Response().apply { resultCode = 400 }
        } else Response().apply { resultCode = -1 }
    }
}