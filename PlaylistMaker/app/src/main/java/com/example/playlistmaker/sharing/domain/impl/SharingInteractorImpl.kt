package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun writeToSupport() {
        externalNavigator.openEmail(
            email = getSupportEmail(),
            title = getString(context, R.string.title_to_support_message),
            body = getString(context, R.string.text_to_support_message)
        )
    }

    private fun getShareAppLink(): String {
        return getString(context, R.string.url_to_the_course)

    }

    private fun getSupportEmail(): String {
        return getString(context, R.string.support_email)
    }

    private fun getTermsLink(): String {
        return getString(context, R.string.url_to_the_offer)
    }
}
