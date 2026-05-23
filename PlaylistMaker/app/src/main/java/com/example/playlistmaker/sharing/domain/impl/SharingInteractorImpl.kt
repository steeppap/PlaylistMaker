package com.example.playlistmaker.sharing.domain.impl


import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.view_model.ToastState
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val resourceProvider: ResourceProvider,
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {
    
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }
    
    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }
    
    override fun writeToSupport(onUiStateChanged: (ToastState) -> Unit) {
        externalNavigator.openEmail(
            email = getSupportEmail(),
            title = getTitle(),
            mailBody = getMailBody()
        ){ errorMessage ->
            onUiStateChanged(ToastState.ShowToast(errorMessage))
        }
    }
    
    private fun getShareAppLink(): String {
        return resourceProvider.getString(R.string.url_to_the_course)
    }
    private fun getTermsLink(): String {
        return resourceProvider.getString(R.string.url_to_the_offer)
    }
    private fun getSupportEmail(): String {
        return resourceProvider.getString(R.string.support_email)
    }
    private fun getTitle(): String{
        return resourceProvider.getString(R.string.title_to_support_message)
    }
    private fun getMailBody(): String{
        return resourceProvider.getString(R.string.text_to_support_message)
    }
}
