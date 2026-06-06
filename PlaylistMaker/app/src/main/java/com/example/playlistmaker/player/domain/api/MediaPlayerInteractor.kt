package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.models.Track


interface MediaPlayerInteractor {
    fun onPause()
    fun playbackControl(state: Int)
    fun release()
    fun setListener(listener: MediaPlayerListener)
    fun setPreviewUrl(previewUrl: String)
    fun removeListener()
    fun updatePlayerState(newState: Int)
    fun updateProgress(progress: String)
    fun getTrackByPreviewUrl()
    
    interface MediaPlayerListener {
        fun onStateChanged(newState: Int)
        fun onProgressUpdated(progress: String)
        fun onTrackLoaded(track: Track)
    }
}
