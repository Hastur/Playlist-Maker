package com.practicum.playlistmaker.util

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    private val gson = Gson()

    fun <T> createFromJson(json: String, className: Class<T>): T = gson.fromJson(json, className)

    fun serializeToJson(obj: Any): String = gson.toJson(obj)

    fun formatTimeAsString(time: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)

    fun formatYearAsString(date: Date?): String? =
        date?.let { SimpleDateFormat("yyyy", Locale.getDefault()).format(it) }
}