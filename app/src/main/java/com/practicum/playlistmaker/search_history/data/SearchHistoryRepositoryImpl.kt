package com.practicum.playlistmaker.search_history.data

import android.content.Context.MODE_PRIVATE
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.Utils
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search_history.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl :
    SearchHistoryRepository {
    companion object {
        private const val SHARED_PREFERENCE_SEARCH_HISTORY = "SHARED_PREFERENCE_SEARCH_HISTORY"
        private const val HISTORY_TRACK_LIST = "HISTORY_TRACK_LIST"
    }

    private val sharedPreferences =
        Creator.application.getSharedPreferences(SHARED_PREFERENCE_SEARCH_HISTORY, MODE_PRIVATE)

    override fun getTracks(): List<Track> {
        val savedValue = sharedPreferences.getString(HISTORY_TRACK_LIST, null)
        return if (savedValue != null) {
            Utils().createFromJson(savedValue, Array<Track>::class.java).toMutableList()
        } else listOf()
    }

    override fun saveTracks(tracks: List<Track>) {
        sharedPreferences.edit()
            .putString(HISTORY_TRACK_LIST, Utils().serializeToJson(tracks))
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit()?.remove(HISTORY_TRACK_LIST)?.apply()
    }
}