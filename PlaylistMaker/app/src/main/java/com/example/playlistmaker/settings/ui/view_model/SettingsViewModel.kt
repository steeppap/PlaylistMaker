package com.example.playlistmaker.settings.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
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
        sharingInteractor.writeToSupport{ state ->
            toastStateLiveData.postValue(state)
        }
    }
    fun hideToast(){
        toastStateLiveData.postValue(ToastState.HideToast)
    }
    
    companion object {
        fun getFactory(
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sharingInteractor = Creator.provideSharingInteractor(context)
                val themeInteractor = Creator.provideThemeInteractor(context)
                SettingsViewModel(sharingInteractor, themeInteractor)
            }
        }
    }
}
