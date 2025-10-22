package com.practicum.playlistmaker.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.components.SettingsItem
import com.practicum.playlistmaker.compose_resources.components.Toolbar
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val darkThemeState by viewModel.getDarkThemeFlow().collectAsState()

    Scaffold(
        topBar = { Toolbar(stringResource(R.string.settings)) },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                SettingsItem(
                    textRes = R.string.dark_theme,
                    darkThemeState = darkThemeState,
                    clickListener = { viewModel.switchTheme(!darkThemeState) }
                )

                val shareUrl = stringResource(R.string.share_url)
                SettingsItem(
                    textRes = R.string.share,
                    iconRes = R.drawable.ic_share,
                    clickListener = { viewModel.shareApp(shareUrl) }
                )

                val mailAddress = stringResource(R.string.send_mail_address)
                val mailSubject = stringResource(R.string.send_mail_subject)
                val mailText = stringResource(R.string.send_mail_text)
                SettingsItem(
                    textRes = R.string.support,
                    iconRes = R.drawable.ic_support,
                    clickListener = {
                        viewModel.sendMail(
                            address = mailAddress,
                            subject = mailSubject,
                            text = mailText
                        )
                    }
                )

                SettingsItem(
                    textRes = R.string.user_agreement,
                    iconRes = R.drawable.ic_arrow_next,
                    clickListener = {
                        viewModel.openUserAgreement()
                    }
                )
            }
        }
    }
}