package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val SHARED_PREFERENCE_DARK_THEME = "SHARED_PREFERENCE_DARK_THEME"
        const val DARK_THEME = "DARK_THEME"
    }

    var darkTheme = false
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_DARK_THEME, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(
            DARK_THEME,
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        )
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        sharedPreferences.edit().putBoolean(DARK_THEME, darkThemeEnabled).apply()
    }
}