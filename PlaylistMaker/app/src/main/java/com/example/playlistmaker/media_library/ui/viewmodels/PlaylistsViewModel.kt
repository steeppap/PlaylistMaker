package com.example.playlistmaker.media_library.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor
import com.example.playlistmaker.media_library.ui.extension.PlaylistsUiMapper
import com.example.playlistmaker.media_library.ui.states.PlaylistState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData
    
    init {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    stateLiveData.postValue(PlaylistState.Empty)
                } else {
                    stateLiveData.postValue(PlaylistState.Content(PlaylistsUiMapper.playlistsToPlaylistsUi(playlists)))
                }
            }
        }
    }
}
