package com.example.playlistmaker.search.data.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchDataCreator {
    private const val PREFS_NAME = "app_prefs"
    fun createGson(): Gson {
        return Gson()
    }
    
    fun createSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun createITunesService(baseUrl: String): ITunesApiService {
        return initRetrofit(baseUrl).create(ITunesApiService::class.java)
    }
    
    fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(createSharedPrefs(context))
    }
    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }
    private fun initRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
