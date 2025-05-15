package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.run {
            toolbarSettings.setNavigationOnClickListener {
                this@SettingsActivity.finish()
            }

            nightModeSwitch.setOnCheckedChangeListener { _, checked ->
                viewModel.switchTheme(checked)
            }

            settingsShare.setOnClickListener {
                viewModel.shareApp()
            }

            settingsSupport.setOnClickListener {
                viewModel.sendMail(
                    application.getString(R.string.send_mail_address),
                    application.getString(R.string.send_mail_subject),
                    application.getString(R.string.send_mail_text)
                )
            }

            settingsUserAgreement.setOnClickListener {
                viewModel.openUserAgreement()
            }
        }

        viewModel.getDarkThemeEnabledLiveData().observe(this) { isEnabled ->
            binding.nightModeSwitch.isChecked = isEnabled
        }
    }
}