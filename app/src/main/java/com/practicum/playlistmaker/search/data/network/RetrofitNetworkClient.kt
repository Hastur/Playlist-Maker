package com.practicum.playlistmaker.search.data.network

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

    override fun makeRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val response = searchService.searchTrack(dto.searchText).execute()
            val body = response.body() ?: Response()
            return body.apply { resultCode = response.code() }
        }
        else return Response().apply { resultCode = 400 }
    }
}