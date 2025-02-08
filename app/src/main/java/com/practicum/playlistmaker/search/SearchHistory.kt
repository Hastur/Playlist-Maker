package com.practicum.playlistmaker.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson

@SuppressLint("NotifyDataSetChanged")
class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) {
    companion object {
        const val HISTORY_TRACK_LIST = "HISTORY_TRACK_LIST"
    }

    var trackList = mutableListOf<Track>()
    val historyAdapter = SearchAdapter()

    fun getTracks() {
        val savedValue = sharedPreferences.getString(HISTORY_TRACK_LIST, null)
        if (savedValue != null) {
            trackList = createFromJson(savedValue).toMutableList()
            historyAdapter.trackList = trackList.asReversed()
        }
    }

    fun addTrack(track: Track) {
        if (trackList.any { it.trackId == track.trackId }) {
            trackList.remove(trackList.first { it.trackId == track.trackId })
        }
        if (trackList.size >= 10) trackList.removeAt(0)
        trackList.add(track)
        historyAdapter.notifyDataSetChanged()

        sharedPreferences.edit().putString(HISTORY_TRACK_LIST, serialiseToJson(trackList)).apply()

        Toast.makeText(context, "Трек вошёл в историю", Toast.LENGTH_SHORT).show()
    }

    fun clearHistory() {
        trackList.clear()

        sharedPreferences.edit().remove(HISTORY_TRACK_LIST).apply()
    }

    private fun createFromJson(json: String) = Gson().fromJson(json, Array<Track>::class.java)

    private fun serialiseToJson(trackList: List<Track>) = Gson().toJson(trackList)

}