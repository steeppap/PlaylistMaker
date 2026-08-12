package com.example.playlistmaker.media_library.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.search.ui.extension.TrackListUiMapper
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoriteState>()
    fun observeStateLiveData(): LiveData<FavoriteState> = stateLiveData
    
    init {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks()
                .collect { tracks ->
                    val tracksUi = TrackListUiMapper.trackListToTrackListUi(tracks)
                    if (tracksUi.isNotEmpty()) {
                        stateLiveData.postValue(FavoriteState.WithTracks(tracksUi))
                    } else {
                        stateLiveData.postValue(FavoriteState.Empty)
                    }
                }
        }
    }
}
