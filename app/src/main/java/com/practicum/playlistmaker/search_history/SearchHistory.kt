package com.practicum.playlistmaker.search_history

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.practicum.playlistmaker.Utils
import com.practicum.playlistmaker.search.domain.models.Track

@SuppressLint("NotifyDataSetChanged")
class SearchHistory(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val HISTORY_TRACK_LIST = "HISTORY_TRACK_LIST"
    }

    private var trackList = mutableListOf<Track>()

    fun getTracks(): List<Track> {
        val savedValue = sharedPreferences.getString(HISTORY_TRACK_LIST, null)
        if (savedValue != null) {
            trackList = Utils().createFromJson(savedValue, Array<Track>::class.java).toMutableList()
        }
        return trackList
    }

    fun addTrack(track: Track): List<Track> {
        if (trackList.any { it.trackId == track.trackId }) {
            trackList.remove(trackList.first { it.trackId == track.trackId })
        }
        if (trackList.size >= 10) trackList.removeAt(0)
        trackList.add(track)
        sharedPreferences.edit().putString(HISTORY_TRACK_LIST, Utils().serializeToJson(trackList))
            .apply()
        return trackList
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_TRACK_LIST).apply()
    }
}