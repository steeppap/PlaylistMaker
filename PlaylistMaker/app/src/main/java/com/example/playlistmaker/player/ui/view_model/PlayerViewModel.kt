package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.state.PlayerStateWithProgress
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.extension.TrackUiMapper
import com.example.playlistmaker.search.ui.models.TrackUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds


class PlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    previewUrl: String
) : ViewModel(),
    MediaPlayerInteractor.MediaPlayerListener {
    
    private var timerJob: Job? = null
    private val playerStateWithProgressLiveData = MutableLiveData(
        PlayerStateWithProgress(STATE_DEFAULT, DEFAULT_TIME)
    )
    
    fun observePlayerStateWithProgress(): LiveData<PlayerStateWithProgress> =
        playerStateWithProgressLiveData
    
    private val trackUiModelLiveData = MutableLiveData<TrackUiModel>()
    fun observeTrackUiModel(): LiveData<TrackUiModel> = trackUiModelLiveData
    
    init {
        mediaPlayerInteractor.setPreviewUrl(previewUrl)
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
        
        startProgressUpdate(playerStateWithProgressLiveData.value?.playerState)
    }
    
    override fun onTrackLoaded(track: Track) {
        trackUiModelLiveData.value = TrackUiMapper.trackToTrackUiModel(track)
    }
    
    private fun startProgressUpdate(state: Int?) {
        when (state) {
            
            STATE_PLAYING -> timerJob = viewModelScope.launch {
                while (true) {
                    delay(TIMER_DELAY.milliseconds)
                    val current = playerStateWithProgressLiveData.value
                    playerStateWithProgressLiveData.value =
                        current?.copy(progressTime = currentPosition())
                }
            }
            
            STATE_PAUSED -> timerJob?.cancel()
            
            STATE_PREPARED, STATE_DEFAULT -> {
                timerJob?.cancel()
                val current = playerStateWithProgressLiveData.value
                playerStateWithProgressLiveData.value =
                    current?.copy(progressTime = DEFAULT_TIME)
                
            }
        }
    }
    
    private fun currentPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayerInteractor.getCurrentPosition())
    }
    
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DEFAULT_TIME = "00:00"
        const val TIMER_DELAY = 300L
    }
}
