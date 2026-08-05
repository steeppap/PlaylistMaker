package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PAUSED
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PLAYING
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PREPARED

class MediaPlayerInteractorImpl(
    private val mediaPlayer: MediaPlayer
) :
    MediaPlayerInteractor {
    private var listener: MediaPlayerInteractor.MediaPlayerListener? = null
    
    
    override fun onPause() {
        pausePlayer()
    }
    
    override fun playbackControl(state: Int) {
        when (state) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    
    override fun setListener(listener: MediaPlayerInteractor.MediaPlayerListener) {
        this.listener = listener
    }
    
    override fun setPreviewUrl(previewUrl: String?) {
        preparePlayer(previewUrl)
    }
    
    override fun removeListener() {
        listener = null
    }
    
    override fun updatePlayerState(newState: Int) {
        listener?.onStateChanged(newState)
    }
    
    override fun release() {
        mediaPlayer.release()
    }
    
    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    
    private fun preparePlayer(previewUrl: String?) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        
        mediaPlayer.setOnPreparedListener {
            updatePlayerState(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            updatePlayerState(STATE_PREPARED)
        }
    }
    
    private fun startPlayer() {
        mediaPlayer.start()
        updatePlayerState(STATE_PLAYING)
    }
    
    private fun pausePlayer() {
        mediaPlayer.pause()
        updatePlayerState(STATE_PAUSED)
    }
}
