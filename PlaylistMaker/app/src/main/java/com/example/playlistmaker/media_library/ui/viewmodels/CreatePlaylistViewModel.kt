package com.example.playlistmaker.media_library.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor

class CreatePlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val titleEditTextLiveData = MutableLiveData("")
    fun observeTitleEditText(): LiveData<String> = titleEditTextLiveData
    private val descEditTextLiveData = MutableLiveData("")
    fun observeDescEditText(): LiveData<String> = descEditTextLiveData
    private val coverPathLiveData = MutableLiveData<String?>()
    
    fun updateTitleEditText(title: String) {
        titleEditTextLiveData.postValue(title)
    }
    
    fun updateDescEditText(description: String) {
        descEditTextLiveData.postValue(description)
    }
    
    fun setCoverPath(path: String) {
        coverPathLiveData.value = path
    }
    
    suspend fun createPlaylist() {
        playlistInteractor.createPlaylist(
            titleEditTextLiveData.value!!.trim(),
            descEditTextLiveData.value?.trim(),
            coverPathLiveData.value
        )
    }
}
