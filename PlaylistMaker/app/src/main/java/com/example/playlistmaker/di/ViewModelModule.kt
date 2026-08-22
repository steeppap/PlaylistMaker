package com.example.playlistmaker.di

import androidx.lifecycle.SavedStateHandle
import com.example.playlistmaker.media_library.ui.viewmodels.CreatePlaylistViewModel
import com.example.playlistmaker.media_library.ui.viewmodels.EditPlaylistViewModel
import com.example.playlistmaker.media_library.ui.viewmodels.FavoriteTracksViewModel
import com.example.playlistmaker.media_library.ui.viewmodels.PlaylistViewModel
import com.example.playlistmaker.media_library.ui.viewmodels.PlaylistsViewModel
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.models.TrackUiModel
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    
    viewModel { (track: TrackUiModel) ->
        PlayerViewModel(
            mediaPlayerInteractor = get<MediaPlayerInteractor>(),
            favoriteTracksInteractor = get(),
            playlistInteractor = get(),
            track = track
        )
    }
    
    viewModel {
        SearchViewModel(get(), get())
    }
    
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }
    
    viewModel {
        PlaylistsViewModel(get())
    }
    
    viewModel {(savedStateHandle: SavedStateHandle) ->
        CreatePlaylistViewModel(savedStateHandle,get())
    }
    viewModel {(playlistId: Long) ->
        PlaylistViewModel(get(), playlistId)
    }
}
