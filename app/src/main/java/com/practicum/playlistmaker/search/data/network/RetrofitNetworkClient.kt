package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val searchService = retrofit.create(TrackSearchApi::class.java)

    private fun isNetworkAvailable(): Boolean {
        val manager =
            Creator.application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    override fun makeRequest(dto: Any): Response {
        if (isNetworkAvailable()) {
            if (dto is TrackSearchRequest) {
                val response = searchService.searchTrack(dto.searchText).execute()
                val body = response.body() ?: Response()
                return body.apply { resultCode = response.code() }
            } else return Response().apply { resultCode = 400 }
        } else return Response().apply { resultCode = -1 }
    }
}