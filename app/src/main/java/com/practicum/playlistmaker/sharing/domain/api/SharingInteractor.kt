package com.practicum.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun sendMail(address: String, subject: String, text: String)
    fun openUserAgreement()
    fun shareMessage(message: String)
}