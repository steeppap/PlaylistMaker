package com.example.playlistmaker.sharing.domain.api

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(email: String, title: String , body: String)
}
