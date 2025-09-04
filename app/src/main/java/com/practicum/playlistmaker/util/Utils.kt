package com.practicum.playlistmaker.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    private val gson = Gson()

    fun <T> createFromJson(json: String, className: Class<T>): T = gson.fromJson(json, className)

    fun createIntListFromJson(json: String): List<Int> {
        val type: Type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, type)
    }

    fun serializeToJson(obj: Any): String = gson.toJson(obj)

    fun formatTimeAsString(time: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)

    fun formatYearAsString(date: Date?): String? =
        date?.let { SimpleDateFormat("yyyy", Locale.getDefault()).format(it) }

    fun isNetworkAvailable(context: Context): Boolean {
        val manager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = manager.activeNetwork ?: return false
        val activeNetwork = manager.getNetworkCapabilities(capabilities) ?: return false
        val connected = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return connected
    }
}