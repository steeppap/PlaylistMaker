package com.example.playlistmaker.sharing.data.impl

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(link: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        context.startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(link: String) {

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = link.toUri()
        }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openEmail(email: String, title: String, mailBody: String, onError: (String) -> Unit) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, mailBody)
        }

        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: ActivityNotFoundException) {
            onError(getString(context, R.string.toast_no_email_app))
        }
    }
}
