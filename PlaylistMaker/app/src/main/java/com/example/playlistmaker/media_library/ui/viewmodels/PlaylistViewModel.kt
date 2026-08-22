package com.example.playlistmaker.media_library.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor
import com.example.playlistmaker.media_library.ui.models.PlaylistUi
import com.example.playlistmaker.search.ui.extension.TrackListUiMapper
import com.example.playlistmaker.search.ui.models.TrackUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor, playlistId: Long) :
    ViewModel() {
    
    private val playlistLiveData = MutableLiveData<PlaylistUi>()
    fun observePlaylist(): LiveData<PlaylistUi> = playlistLiveData
    private val tracksPlaylistUiLiveData = MutableLiveData<List<TrackUiModel>>()
    fun observeTracksPlaylistUi(): LiveData<List<TrackUiModel>> = tracksPlaylistUiLiveData
    private val playlistDurationLiveData = MutableLiveData<Long>()
    fun observePlaylistDuration(): LiveData<Long> = playlistDurationLiveData
    
    init {
        viewModelScope.launch {
            getPlaylistById(playlistId)
        }
    }
    
    suspend fun getPlaylistById(playlistId: Long) {
        playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
            playlistLiveData.postValue(playlist)
            
            getTracksByIds(playlist.tracksIds)
        }
    }
    
    suspend fun getTracksByIds(tracksIds: String?) {
        if (tracksIds == "[]") {
            playlistDurationLiveData.postValue(0L)
            tracksPlaylistUiLiveData.postValue(emptyList())
            return
        }
        
        val tracksList = playlistInteractor.getTracksByIds(tracksIds).first()
        val tracksPlayListUi = TrackListUiMapper.tracksPlaylistToTrackListUi(tracksList)
        
        tracksPlaylistUiLiveData.postValue(tracksPlayListUi)
        
        val totalDurationMs = tracksPlayListUi.sumOf { it.trackTimeMillis }.toLong()
        playlistDurationLiveData.postValue(totalDurationMs)
    }
    
    fun removeTrackFromPlaylist(trackId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.removeTrackFromPlaylist(trackId, playlistLiveData.value!!)
        }
    }
    
    fun removePlaylistById() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.removePlaylistById(playlistLiveData.value?.id!!)
        }
    }
}
