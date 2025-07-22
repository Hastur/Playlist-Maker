package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {

    override fun sendMail(address: String, subject: String, text: String) =
        externalNavigator.sendMail(EmailData(address, subject, text))

    override fun openUserAgreement() = externalNavigator.openUserAgreement()

    override fun shareMessage(message: String) = externalNavigator.shareMessage(message)

}