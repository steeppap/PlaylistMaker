package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.domainModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, viewModelModule)
        }
        val themeInteractor: ThemeInteractor = get()
        themeInteractor.setDarkModeEnabled(themeInteractor.isDarkModeEnabled())
    }
}
