package com.practicum.playlistmaker.search.track_search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.track_search.data.NetworkClient
import com.practicum.playlistmaker.search.track_search.data.dto.Response
import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val searchService: TrackSearchApi,
    private val context: Context
) : NetworkClient {

    private fun isNetworkAvailable(): Boolean {
        val manager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = manager.activeNetwork ?: return false
        val activeNetwork = manager.getNetworkCapabilities(capabilities) ?: return false
        val connected = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return connected
    }

    override suspend fun makeRequest(dto: Any): Response {
        return if (isNetworkAvailable()) {
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