package com.example.playlistmaker.sharing.data.impl

import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val resourceProvider: ResourceProvider,
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {
    
    override fun shareApp() {
        externalNavigator.shareLink(resourceProvider.getShareAppLink())
    }
    
    override fun openTerms() {
        externalNavigator.openLink(resourceProvider.getTermsLink())
    }
    
    override fun writeToSupport(): Result<Unit> {
        return try {
            externalNavigator.openEmail(
                email = resourceProvider.getSupportEmail(),
                title = resourceProvider.getEmailTitle(),
                mailBody = resourceProvider.getEmailBody()
            ) { errorMessage ->
                throw Exception(resourceProvider.getToastText())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
