package com.practicum.playlistmaker.search.track_search.data.network

import com.practicum.playlistmaker.search.track_search.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackSearchApi {
    @GET("/search?entity=song")
    fun searchTrack(@Query("term") searchText: String): Call<TrackSearchResponse>
}