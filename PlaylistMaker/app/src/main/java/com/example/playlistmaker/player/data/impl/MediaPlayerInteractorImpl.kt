package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.DEFAULT_TIME
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PAUSED
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PLAYING
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PREPARED
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(
    private val mediaPlayer: MediaPlayer,
    private val repository: SearchHistoryRepository
) :
    MediaPlayerInteractor {
    private lateinit var previewUrl: String
    private var listener: MediaPlayerInteractor.MediaPlayerListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private val playbackTimeRunnable = Runnable { startTimerUpdate() }
    private var currentTrack: Track? = null
    
    
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
    
    override fun setPreviewUrl(previewUrl: String) {
        this.previewUrl = previewUrl
        preparePlayer()
    }
    
    override fun removeListener() {
        listener = null
    }
    
    override fun updatePlayerState(newState: Int) {
        listener?.onStateChanged(newState)
    }
    
    override fun updateProgress(progress: String) {
        listener?.onProgressUpdated(progress)
    }
    
    override fun getTrackByPreviewUrl() {
        val track = repository.getTrackByPreviewUrl(previewUrl)
        currentTrack = track
        
        track?.let { listener?.onTrackLoaded(it) }
    }
    
    override fun release() {
        mediaPlayer.release()
        resetTimer()
    }
    
    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        
        mediaPlayer.setOnPreparedListener {
            updatePlayerState(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            updatePlayerState(STATE_PREPARED)
            resetTimer()
        }
    }
    
    private fun startPlayer() {
        mediaPlayer.start()
        updatePlayerState(STATE_PLAYING)
        startTimerUpdate()
    }
    
    private fun pausePlayer() {
        mediaPlayer.pause()
        pauseTimer()
        updatePlayerState(STATE_PAUSED)
    }
    
    private fun resetTimer() {
        updateProgress(DEFAULT_TIME)
        handler.removeCallbacks(playbackTimeRunnable)
    }
    
    private fun startTimerUpdate() {
        updateProgress(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                mediaPlayer.currentPosition
            )
        )
        handler.postDelayed(playbackTimeRunnable, DELAY)
    }
    
    private fun pauseTimer() {
        handler.removeCallbacks(playbackTimeRunnable)
    }
    
    companion object {
        const val DELAY = 500L
    }
}
