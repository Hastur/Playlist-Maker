package com.practicum.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun shareApp()
    fun sendMail(address: String, subject: String, text: String)
    fun openUserAgreement()
}