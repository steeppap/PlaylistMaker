package com.example.playlistmaker.domain.api

interface MediaPlayerInteractor {
    fun preparePlayer()
    fun playbackControl()
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun removeCallBacks()
}