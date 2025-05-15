package com.practicum.playlistmaker.settings.data

import android.app.Application
import android.app.Application.MODE_PRIVATE
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val application: Application) : SettingsRepository {
    companion object {
        private const val SHARED_PREFERENCE_DARK_THEME = "SHARED_PREFERENCE_DARK_THEME"
        const val DARK_THEME = "DARK_THEME"
    }

    private val sharedPreferences =
        application.getSharedPreferences(SHARED_PREFERENCE_DARK_THEME, MODE_PRIVATE)

    override fun checkDarkThemeEnabled(): Boolean = sharedPreferences.getBoolean(
        DARK_THEME,
        application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    )

    override fun switchTheme(isDarkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        sharedPreferences.edit().putBoolean(DARK_THEME, isDarkThemeEnabled).apply()
    }
}