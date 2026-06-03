package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor,
) : ViewModel() {
    
    private val darkModeStateLiveData = MutableLiveData<Boolean>()
    fun observeDarkModeState(): LiveData<Boolean> = darkModeStateLiveData
    private val toastStateLiveData = MutableLiveData<ToastState>()
    fun observeToastState(): LiveData<ToastState> = toastStateLiveData
    
    init {
        darkModeStateLiveData.postValue(themeInteractor.isDarkModeEnabled())
    }
    
    fun setNecessaryTheme() {
        val currentState = themeInteractor.isDarkModeEnabled()
        themeInteractor.setDarkModeEnabled(!currentState)
        darkModeStateLiveData.postValue(!currentState)
    }
    
    fun shareApp() {
        sharingInteractor.shareApp()
    }
    
    fun openUserAgreement() {
        sharingInteractor.openTerms()
    }
    
    fun writeToSupport() {
        val result = sharingInteractor.writeToSupport()
        if (result.isSuccess) {
            toastStateLiveData.postValue(ToastState.HideToast)
        } else {
            val exception = result.exceptionOrNull()
            toastStateLiveData.postValue(
                ToastState.ShowToast(exception?.message ?: "Unknown error")
            )
        }
    }
    
    fun hideToast() {
        toastStateLiveData.postValue(ToastState.HideToast)
    }
}
