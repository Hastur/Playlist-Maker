package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun sendMail(emailData: EmailData)
    fun openUserAgreement()
    fun shareMessage(message: String)
}