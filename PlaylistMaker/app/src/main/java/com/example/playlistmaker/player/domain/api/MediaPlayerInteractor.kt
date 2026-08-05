package com.example.playlistmaker.player.domain.api


interface MediaPlayerInteractor {
    fun onPause()
    fun playbackControl(state: Int)
    fun release()
    fun setListener(listener: MediaPlayerListener)
    fun setPreviewUrl(previewUrl: String?)
    fun removeListener()
    fun updatePlayerState(newState: Int)
    fun getCurrentPosition(): Int
    
    interface MediaPlayerListener {
        fun onStateChanged(newState: Int)
    }
}
