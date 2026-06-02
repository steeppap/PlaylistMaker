package com.example.playlistmaker.player.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.data.PlayerStateWithProgress
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.extension.TrackUiMapper
import com.example.playlistmaker.search.ui.models.TrackUiModel


class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel(),
    MediaPlayerInteractor.MediaPlayerListener {
    
    private val playerStateWithProgressLiveData = MutableLiveData(
        PlayerStateWithProgress(STATE_DEFAULT, DEFAULT_TIME)
    )
    
    fun observePlayerStateWithProgress(): LiveData<PlayerStateWithProgress> =
        playerStateWithProgressLiveData
    
    private val trackUiModelLiveData = MutableLiveData<TrackUiModel>()
    fun observeTrackUiModel(): LiveData<TrackUiModel> = trackUiModelLiveData
    
    init {
        mediaPlayerInteractor.setListener(this)
        mediaPlayerInteractor.getTrackByPreviewUrl()
    }
    
    fun playbackControl() {
        mediaPlayerInteractor.playbackControl(playerStateWithProgressLiveData.value!!.playerState)
    }
    
    fun onPause() {
        mediaPlayerInteractor.onPause()
    }
    
    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.release()
        mediaPlayerInteractor.removeListener()
    }
    
    override fun onStateChanged(newState: Int) {
        val current = playerStateWithProgressLiveData.value
        playerStateWithProgressLiveData.value = current?.copy(playerState = newState)
    }
    
    override fun onProgressUpdated(progress: String) {
        val current = playerStateWithProgressLiveData.value
        playerStateWithProgressLiveData.value = current?.copy(progressTime = progress)
    }
    
    override fun onTrackLoaded(track: Track) {
        trackUiModelLiveData.value = TrackUiMapper.trackToTrackUiModel(track)
    }
    
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DEFAULT_TIME = "00:00"
        
        fun getFactory(context: Context, trackId: String?): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val mediaPlayerInteractor =
                        Creator.provideMediaPlayerInteractor(context, trackId)
                    PlayerViewModel(mediaPlayerInteractor)
                }
            }
    }
}
