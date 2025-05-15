package com.practicum.playlistmaker.search.track_search_history.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search_history.data.SearchHistoryStorage

class SharedPreferencesSearchHistoryStorage(
    private val preferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryStorage {

    companion object {
        private const val HISTORY_TRACK_LIST = "HISTORY_TRACK_LIST"
    }

    override fun getTracksFromPrefs(): List<Track> {
        val savedValue = preferences.getString(HISTORY_TRACK_LIST, null)
        return if (savedValue != null) {
            gson.fromJson(savedValue, Array<Track>::class.java).toMutableList()
        } else listOf()
    }

    override fun saveTracksToPrefs(tracks: List<Track>) {
        preferences.edit()
            .putString(HISTORY_TRACK_LIST, gson.toJson(tracks))
            .apply()
    }

    override fun clearTracksPrefs() {
        preferences.edit()?.remove(HISTORY_TRACK_LIST)?.apply()
    }
}