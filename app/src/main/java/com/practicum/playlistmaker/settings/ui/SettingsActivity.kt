package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Toolbar>(R.id.toolbar_settings).setNavigationOnClickListener {
            this.finish()
        }

        val settings = Creator.provideSettingsInteractor()

        val nightModeSwitch = findViewById<SwitchCompat>(R.id.night_mode_switch)
        nightModeSwitch.isChecked = settings.checkDarkThemeEnabled()
        nightModeSwitch.setOnCheckedChangeListener { _, checked ->
            settings.switchTheme(checked)
        }

        findViewById<TextView>(R.id.settings_share).setOnClickListener {
            settings.shareApp()
        }

        findViewById<TextView>(R.id.settings_support).setOnClickListener {
            settings.sendMail()
        }

        findViewById<TextView>(R.id.settings_user_agreement).setOnClickListener {
            settings.openUserAgreement()
        }
    }
}