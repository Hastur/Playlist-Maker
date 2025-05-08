package com.practicum.playlistmaker.settings.data

import android.app.Application.MODE_PRIVATE
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl : SettingsRepository {
    companion object {
        private const val SHARED_PREFERENCE_DARK_THEME = "SHARED_PREFERENCE_DARK_THEME"
        const val DARK_THEME = "DARK_THEME"
    }

    private val application = Creator.application
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

    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, application.getString(R.string.share_url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .type = "text/plain"
        application.startActivity(shareIntent)
    }

    override fun sendMail() {
        val sendMailIntent = Intent(Intent.ACTION_SENDTO)
        sendMailIntent.data = Uri.parse("mailto:")
        sendMailIntent.putExtra(
            Intent.EXTRA_EMAIL,
            application.getString(R.string.send_mail_address)
        )
            .putExtra(Intent.EXTRA_SUBJECT, application.getString(R.string.send_mail_subject))
            .putExtra(Intent.EXTRA_TEXT, application.getString(R.string.send_mail_text))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(sendMailIntent)
    }

    override fun openUserAgreement() {
        application.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(application.getString(R.string.user_agreement_offer))
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}