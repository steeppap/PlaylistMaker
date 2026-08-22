package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.media_library.data.db.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.media_library.domain.db.PlaylistRepository
import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.player.data.db.MIGRATION_2_3
import com.example.playlistmaker.player.data.impl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.player.data.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.ResourceProviderImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    
    single {
        androidContext()
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    
    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }
    
    factory<Gson> { Gson() }
    
    factory<MediaPlayer> {
        MediaPlayer()
    }
    
    single<NetworkClient> {
        RetrofitNetworkClient(iTunesService = get())
    }
    
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            sharedPrefs = get<SharedPreferences>(),
            gson = get<Gson>(),
            appDatabase = get()
        )
    }
    
    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get(), appDatabase = get())
    }
    
    single<ThemeRepository> {
        ThemeRepositoryImpl(sharedPrefs = get())
    }
    
    single<ResourceProvider> {
        ResourceProviderImpl(context = androidContext())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(context = androidContext())
    }
    
    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get<MediaPlayer>())
    }
    
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .addMigrations(MIGRATION_2_3)
            .build()
    }
    
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }
    
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }
    
}
