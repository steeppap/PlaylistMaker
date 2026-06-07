package com.example.playlistmaker.di

import com.example.playlistmaker.media_library.ui.viewmodels.FavoriteTracksViewModel
import com.example.playlistmaker.media_library.ui.viewmodels.PlaylistsViewModel
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    
    viewModel { (previewUrl: String) ->
        PlayerViewModel(
            mediaPlayerInteractor = get<MediaPlayerInteractor>(),
            previewUrl = previewUrl
        )
    }
    
    viewModel {
        SearchViewModel(get(), get())
    }
    
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        FavoriteTracksViewModel()
    }
    
    viewModel {
        PlaylistsViewModel()
    }
}
