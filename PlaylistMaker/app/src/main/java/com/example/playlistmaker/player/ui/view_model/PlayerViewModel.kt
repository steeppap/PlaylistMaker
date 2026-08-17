package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor
import com.example.playlistmaker.media_library.ui.extension.PlaylistUiMapper
import com.example.playlistmaker.media_library.ui.extension.PlaylistsUiMapper
import com.example.playlistmaker.media_library.ui.models.PlaylistUi
import com.example.playlistmaker.media_library.ui.states.UiEvent
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.state.PlayerStateWithProgress
import com.example.playlistmaker.search.ui.extension.TrackUiMapper
import com.example.playlistmaker.search.ui.models.TrackUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds


class PlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    track: TrackUiModel
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
    
    private val favoriteTrackLiveData = MutableLiveData<Boolean>()
    fun observeFavoriteTrack(): LiveData<Boolean> = favoriteTrackLiveData
    
    private val playlistsStateLiveData = MutableLiveData<List<PlaylistUi>>()
    fun observePlaylistsState(): LiveData<List<PlaylistUi>> = playlistsStateLiveData
    
    private val inPlaylistStateLiveData = MutableLiveData<UiEvent.ShowToast?>()
    fun observeInPlaylistState(): LiveData<UiEvent.ShowToast?> = inPlaylistStateLiveData
    
    init {
        mediaPlayerInteractor.setPreviewUrl(track.previewUrl)
        mediaPlayerInteractor.setListener(this)
        trackUiModelLiveData.postValue(track)
        
        viewModelScope.launch {
            val isFavorite = favoriteTracksInteractor.isInFavoriteById(track.trackId!!)
            favoriteTrackLiveData.postValue(isFavorite)
            
            playlistInteractor.getAllPlaylists().collect { playlists ->
                val playlistsUi = PlaylistsUiMapper.playlistsToPlaylistsUi(playlists)
                playlistsStateLiveData.postValue(playlistsUi)
            }
        }
    }
    
    fun playbackControl() {
        mediaPlayerInteractor.playbackControl(playerStateWithProgressLiveData.value!!.playerState)
    }
    
    fun onPause() {
        mediaPlayerInteractor.onPause()
    }
    
    fun addTrackToPlaylist(playlist: PlaylistUi) {
        val track = trackUiModelLiveData.value ?: return
        
        viewModelScope.launch {
            val isInPlaylist = withContext(Dispatchers.IO) {
                playlistInteractor.addTrackToPlaylist(
                    TrackUiMapper.trackUiModelToTrackPlaylist(track),
                    PlaylistUiMapper.playlistUiToPlaylist(playlist)
                )
            }
            
            if (isInPlaylist) {
                inPlaylistStateLiveData.value =
                    UiEvent.ShowToast(playlist.title, false)
            } else {
                inPlaylistStateLiveData.value =
                    UiEvent.ShowToast(playlist.title, true)
            }
        }
    }
    
    fun clearInPlaylistEvent() {
        inPlaylistStateLiveData.value = null
    }
    
    suspend fun toggleFavorite() {
        val currentTrack = trackUiModelLiveData.value
        val track = TrackUiMapper.trackUiModelToTrack(currentTrack)
        
        if (favoriteTrackLiveData.value == true) {
            favoriteTracksInteractor.removeFromFavorite(track)
            trackUiModelLiveData.postValue(currentTrack?.copy(isFavorite = false))
            favoriteTrackLiveData.postValue(false)
            
        } else {
            favoriteTracksInteractor.addToFavorite(track)
            trackUiModelLiveData.postValue(currentTrack?.copy(isFavorite = true))
            favoriteTrackLiveData.postValue(true)
        }
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
