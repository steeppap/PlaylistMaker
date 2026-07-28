package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.DEFAULT_TIME
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PAUSED
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PLAYING
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel.Companion.STATE_PREPARED
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class MediaPlayerInteractorImpl(
    private val mediaPlayer: MediaPlayer,
    private val repository: SearchHistoryRepository
) :
    MediaPlayerInteractor {
    private lateinit var previewUrl: String
    private var listener: MediaPlayerInteractor.MediaPlayerListener? = null
    private var currentTrack: Track? = null
    private var timerJob: Job? = null
    
    
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
        cancelTimer()
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
        cancelTimer()
    }
    
    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        
        mediaPlayer.setOnPreparedListener {
            updatePlayerState(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            updatePlayerState(STATE_PREPARED)
            cancelTimer()
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
    
    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        updateProgress(DEFAULT_TIME)
    }
    
    private fun startTimerUpdate() {
        timerJob = CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
            while (mediaPlayer.isPlaying) {
                delay(DELAY.milliseconds)
                val progress = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                    mediaPlayer.currentPosition
                )
                withContext(Dispatchers.Main) {
                    updateProgress(progress)
                }
            }
        }
    }
    
    private fun pauseTimer() {
        timerJob?.cancel()
    }
    
    companion object {
        const val DELAY = 300L
    }
}
