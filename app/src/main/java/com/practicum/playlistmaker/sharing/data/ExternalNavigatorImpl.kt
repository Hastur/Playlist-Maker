package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .type = "text/plain"
        context.startActivity(shareIntent)
    }

    override fun sendMail(emailData: EmailData) {
        val sendMailIntent = Intent(Intent.ACTION_SENDTO)
        sendMailIntent.data = Uri.parse("mailto:")
        sendMailIntent.putExtra(Intent.EXTRA_EMAIL, emailData.address)
            .putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            .putExtra(Intent.EXTRA_TEXT, emailData.text)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(sendMailIntent)
    }

    override fun openUserAgreement() {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.user_agreement_offer))
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}