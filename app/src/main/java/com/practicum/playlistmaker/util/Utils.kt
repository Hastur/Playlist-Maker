package com.practicum.playlistmaker.util

import com.google.gson.Gson

class Utils {
    private val gson = Gson()

    fun <T> createFromJson(json: String, className: Class<T>): T = gson.fromJson(json, className)

    fun serializeToJson(obj: Any): String = gson.toJson(obj)
}