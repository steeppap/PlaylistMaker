package com.example.playlistmaker

import android.content.Context
import android.content.res.Resources
import android.widget.ImageButton
import android.widget.TextView
import com.example.playlistmaker.data.SharedPrefsRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksSearchInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksSearchInteractorImpl
import com.example.playlistmaker.domain.models.Track
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    fun createITunesService(baseUrl: String): ITunesApiService {
        return initRetrofit(baseUrl).create(ITunesApiService::class.java)
    }

    fun provideTracksSearchInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksRepository())
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(createSharedPrefs(context))
    }

    fun createSharedPrefs(context: Context): SharedPrefsRepository {
        return SharedPrefsRepositoryImpl(context)
    }

    fun provideMediaPlayerInteractor(
        currentTrack: Track?,
        playStopBtn: ImageButton,
        timeBelowPlayStopBtn: TextView
    ): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(currentTrack, playStopBtn, timeBelowPlayStopBtn)
    }

    private fun initRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(createSharedPrefs(context))
    }
}
