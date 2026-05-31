package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.ResourceProvider

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    
    override fun getShareAppLink(): String {
        return context.getString(R.string.url_to_the_course)
    }
    
    override fun getTermsLink(): String {
        return context.getString(R.string.url_to_the_offer)
    }
    
    override fun getSupportEmail(): String {
        return context.getString(R.string.support_email)
    }
    
    override fun getEmailTitle(): String {
        return context.getString(R.string.title_to_support_message)
    }
    
    override fun getEmailBody(): String {
        return context.getString(R.string.text_to_support_message)
    }
    
    override fun getToastText(): String {
        return context.getString(R.string.toast_no_email_app)
    }
}
