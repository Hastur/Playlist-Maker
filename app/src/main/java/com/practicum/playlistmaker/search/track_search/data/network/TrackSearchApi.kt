package com.practicum.playlistmaker.search.track_search.data.network

import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackSearchApi {
    @GET("/search?entity=song")
    suspend fun searchTrack(@Query("term") searchText: String): TrackSearchResponse
}