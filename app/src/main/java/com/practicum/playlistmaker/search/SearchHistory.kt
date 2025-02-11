package com.practicum.playlistmaker.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.player.PlayerActivity
import com.practicum.playlistmaker.search.SearchActivity.Companion.TRACK

@SuppressLint("NotifyDataSetChanged")
class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) {
    companion object {
        const val HISTORY_TRACK_LIST = "HISTORY_TRACK_LIST"
    }

    var trackList = mutableListOf<Track>()
    val historyAdapter = SearchAdapter { track ->
        context.startActivity(
            Intent(context, PlayerActivity::class.java).putExtra(
                TRACK,
                (context as App).serializeToJson(track)
            )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun getTracks() {
        val savedValue = sharedPreferences.getString(HISTORY_TRACK_LIST, null)
        if (savedValue != null) {
            trackList = (context as App).createFromJson(savedValue, Array<Track>::class.java)
                .toMutableList()
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

        sharedPreferences.edit()
            .putString(HISTORY_TRACK_LIST, (context as App).serializeToJson(trackList)).apply()

        Toast.makeText(context, "Трек вошёл в историю", Toast.LENGTH_SHORT).show()
    }

    fun clearHistory() {
        trackList.clear()

        sharedPreferences.edit().remove(HISTORY_TRACK_LIST).apply()
    }

}