package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.setApplication(this)

        val themeSettings = Creator.provideSettingsInteractor()
        themeSettings.switchTheme(themeSettings.checkDarkThemeEnabled())
    }
}