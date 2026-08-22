package com.example.playlistmaker.media_library.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistInteractor: PlaylistInteractor
) :
    BaseViewModel(savedStateHandle) {
    private val playlistId: Long? = savedStateHandle["playlist_id"]
    
    private val isEditModeLiveData = MutableLiveData(playlistId != null)
    fun observeIsEditMode(): LiveData<Boolean> = isEditModeLiveData
    private val titleLiveData = MutableLiveData<String>()
    fun observeTitle(): LiveData<String> = titleLiveData
    private val descriptionLiveData = MutableLiveData<String>()
    fun observeDescription(): LiveData<String> = descriptionLiveData
    
//    init {
//        if (playlistId != null) {
//            viewModelScope.launch {
//                val playlist = playlistInteractor.getPlaylistById(playlistId)
//
//                if (playlist != null) {
//                    titleLiveData.value = playlist.
//                    descriptionLiveData.value = playlist.description
//                }
//                _cover.value = playlist.cover
//
//            }
//        }
//    }

//    fun onSaveClick() {
//        val newTitle = _title.value ?: return
//        val newDesc = _description.value ?: return
//
//// Создаем объект для сохранения
//        val playlistToSave = if (playlistId != null) {
//            Playlist(id = playlistId, title = newTitle, description = newDesc
//        }
//    }
}
