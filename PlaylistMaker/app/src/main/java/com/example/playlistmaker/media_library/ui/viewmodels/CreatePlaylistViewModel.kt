package com.example.playlistmaker.media_library.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistInteractor: PlaylistInteractor
) : BaseViewModel(savedStateHandle) {
    private val playlistId: Long? = savedStateHandle["playlist_id"]
    private val isEditModeLiveData = MutableLiveData(playlistId != null)
    fun observeIsEditMode(): LiveData<Boolean> = isEditModeLiveData
    private val titleEditTextLiveData = MutableLiveData("")
    fun observeTitleEditText(): LiveData<String> = titleEditTextLiveData
    private val descEditTextLiveData = MutableLiveData("")
    fun observeDescEditText(): LiveData<String> = descEditTextLiveData
    private val coverPathLiveData = MutableLiveData<String?>()
    fun observeCoverPath(): LiveData<String?> = coverPathLiveData
    
    init {
        if (playlistId != null) {
            viewModelScope.launch {
                getPlaylistById(playlistId)
            }
        }
    }
    
    suspend fun getPlaylistById(playlistId: Long) {
        playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
            titleEditTextLiveData.postValue(playlist.title)
            descEditTextLiveData.postValue(playlist.description)
            setCoverPath(playlist.coverPath)
        }
    }
    
    fun updateTitleEditText(title: String) {
        titleEditTextLiveData.postValue(title)
    }
    
    fun updateDescEditText(description: String) {
        descEditTextLiveData.postValue(description)
    }
    
    fun setCoverPath(path: String?) {
        coverPathLiveData.value = path
    }
    
    suspend fun createPlaylist() {
        playlistInteractor.createPlaylist(
            titleEditTextLiveData.value!!.trim(),
            descEditTextLiveData.value?.trim(),
            coverPathLiveData.value
        )
    }
    
    suspend fun savePlaylistInfo() {
        playlistInteractor.updatePlaylistInfo(
            playlistId!!,
            titleEditTextLiveData.value!!,
            descEditTextLiveData.value,
            coverPathLiveData.value
        )
    }
}
