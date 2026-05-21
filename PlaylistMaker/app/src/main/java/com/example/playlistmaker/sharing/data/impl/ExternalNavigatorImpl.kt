package com.example.playlistmaker.sharing.data.impl

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
        context.startActivity(Intent.createChooser(intent, null))
    }

    override fun openLink(link: String) {

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = link.toUri()
        }
        context.startActivity(intent)
    }

    override fun openEmail(email: String, title: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                getString(context,R.string.toast_no_email_app),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
