package com.example.playlistmaker.sharing.domain.api

interface ResourceProvider {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmail(): String
    fun getEmailTitle(): String
    fun getEmailBody(): String
    fun getToastText(): String
}
