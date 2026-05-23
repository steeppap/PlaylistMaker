package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.data.PlayerStateWithProgress


class PlayerViewModel(url: String) : ViewModel(), MediaPlayerInteractor.MediaPlayerListener {
    private val mediaPlayerInteractor: MediaPlayerInteractor =
        Creator.provideMediaPlayerInteractor(url)
    private val playerStateWithProgressLiveData = MutableLiveData(
        PlayerStateWithProgress(STATE_DEFAULT, DEFAULT_TIME)
    )
    
    init {
        mediaPlayerInteractor.setListener(this)
    }
    
    fun observePlayerStateWithProgress(): LiveData<PlayerStateWithProgress> =
        playerStateWithProgressLiveData
    
    
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
    
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DEFAULT_TIME = "00:00"
        
        fun getFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }
}
