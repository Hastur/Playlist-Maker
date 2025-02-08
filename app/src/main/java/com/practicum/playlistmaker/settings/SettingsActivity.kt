package com.practicum.playlistmaker.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.App
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

        val nightModeSwitch = findViewById<SwitchCompat>(R.id.night_mode_switch)
        nightModeSwitch.isChecked = (applicationContext as App).darkTheme
        nightModeSwitch.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        findViewById<TextView>(R.id.settings_share).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url))
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }

        findViewById<TextView>(R.id.settings_support).setOnClickListener {
            val sendMailIntent = Intent(Intent.ACTION_SENDTO)
            sendMailIntent.data = Uri.parse("mailto:")
            sendMailIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.send_mail_address))
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_mail_subject))
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.send_mail_text))
            startActivity(sendMailIntent)
        }

        findViewById<TextView>(R.id.settings_user_agreement).setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_offer)))
            )
        }
    }
}