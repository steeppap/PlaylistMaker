package com.example.playlistmaker.di

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksSearchInteractor
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksSearchInteractorImpl
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    factory<SharingInteractor> {
        SharingInteractorImpl(resourceProvider = get(), externalNavigator = get() )
    }
    
    factory<TracksSearchInteractor> {
        TracksSearchInteractorImpl(repository = get())
    }
    
    factory<ThemeInteractor>{
        ThemeInteractorImpl(themeRepository = get())
    }
    
    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }
}
