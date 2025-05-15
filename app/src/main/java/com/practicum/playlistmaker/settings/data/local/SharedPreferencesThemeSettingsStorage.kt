package com.practicum.playlistmaker.settings.data.local

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.practicum.playlistmaker.settings.data.ThemeSettingsStorage

class SharedPreferencesThemeSettingsStorage(
    private val preferences: SharedPreferences,
    private val context: Context
) : ThemeSettingsStorage {

    companion object {
        private const val DARK_THEME = "DARK_THEME"
    }

    override fun getDarkTheme(): Boolean = preferences.getBoolean(
        DARK_THEME,
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    )

    override fun saveThemeSettings(isDarkThemeEnabled: Boolean) {
        preferences.edit().putBoolean(DARK_THEME, isDarkThemeEnabled).apply()
    }
}