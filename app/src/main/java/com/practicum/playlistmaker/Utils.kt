package com.practicum.playlistmaker

import com.google.gson.Gson

class Utils {
    private val gson = Gson()

    fun <T> createFromJson(json: String, className: Class<T>) = gson.fromJson(json, className)

    fun serializeToJson(obj: Any) = gson.toJson(obj)
}