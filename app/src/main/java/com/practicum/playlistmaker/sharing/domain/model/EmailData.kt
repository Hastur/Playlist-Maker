package com.practicum.playlistmaker.sharing.domain.model

data class EmailData(
    val address: String,
    val subject: String,
    val text: String
)