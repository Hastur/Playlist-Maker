package com.practicum.playlistmaker.search

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("/search?entity=song")
    fun searchTrack(@Query("term") searchText: String): Call<SearchResponse>
}