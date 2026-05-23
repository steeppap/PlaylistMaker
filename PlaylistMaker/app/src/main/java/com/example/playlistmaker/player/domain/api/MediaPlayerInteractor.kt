package com.example.playlistmaker.player.domain.api


interface MediaPlayerInteractor {
    fun onPause()
    fun playbackControl(state: Int)
    fun release()
    fun setListener(listener: MediaPlayerListener)
    fun removeListener()
    fun updatePlayerState(newState: Int)
    fun updateProgress(progress: String)
    
    interface MediaPlayerListener {
        fun onStateChanged(newState: Int)
        fun onProgressUpdated(progress: String)
    }
}
