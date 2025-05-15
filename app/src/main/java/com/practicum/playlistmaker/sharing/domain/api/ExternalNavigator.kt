package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareApp()
    fun sendMail(emailData: EmailData)
    fun openUserAgreement()
}