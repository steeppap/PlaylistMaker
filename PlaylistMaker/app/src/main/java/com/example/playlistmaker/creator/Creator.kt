package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.data.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.search.data.creator.SearchDataCreator
import com.example.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.api.TracksSearchInteractor
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksSearchInteractorImpl
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.ResourceProviderImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    
    fun provideTracksSearchInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksRepository())
    }
    
    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }
    
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
    
    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ResourceProviderImpl(context), getExternalNavigator(context))
    }
    
    fun provideMediaPlayerInteractor(url: String): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(url)
    }
    
    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
    
    private fun getTracksRepository(): TracksRepository {
        return SearchDataCreator.getTracksRepository()
    }
    
    private fun getThemeRepository(context: Context): ThemeRepository {
        return SearchDataCreator.getThemeRepository(context)
    }
    
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            SearchDataCreator.createSharedPrefs(context),
            SearchDataCreator.createGson()
        )
    }
}
