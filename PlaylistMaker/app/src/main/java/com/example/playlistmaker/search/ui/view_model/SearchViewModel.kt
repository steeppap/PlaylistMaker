package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksSearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.extension.TrackListUiMapper
import com.example.playlistmaker.search.ui.extension.TrackUiMapper
import com.example.playlistmaker.search.ui.models.TrackUiModel
import kotlinx.coroutines.launch

class SearchViewModel(
    val searchHistoryInteractor: SearchHistoryInteractor,
    val tracksSearchInteractor: TracksSearchInteractor
) : ViewModel() {
    private val searchStateLiveData = MutableLiveData<TrackSearchState>(TrackSearchState.Default)
    fun observeSearchState(): LiveData<TrackSearchState> = searchStateLiveData
    private val trackListLiveData = MutableLiveData(emptyList<TrackUiModel>())
    fun observeTrackList(): LiveData<List<TrackUiModel>> = trackListLiveData
    private val historyLiveData = MutableLiveData<List<TrackUiModel>>()
    fun observeHistory(): LiveData<List<TrackUiModel>> = historyLiveData
    private val searchQueryLiveData = MutableLiveData("")
    fun observeSearchQuery(): LiveData<String> = searchQueryLiveData
    private val clearButtonVisibleLiveData = MutableLiveData(false)
    fun observeClearButtonVisible(): LiveData<Boolean> = clearButtonVisibleLiveData
    
    init {
        viewModelScope.launch{
            val history = getTracksHistory()
            historyLiveData.value = history
            
            if (history.isNotEmpty()) {
                searchStateLiveData.postValue(TrackSearchState.History)
            }
        }
    }
    suspend fun addTrackToHistory(trackUi: TrackUiModel) {
        searchHistoryInteractor.addTrackToHistory(TrackUiMapper.trackUiModelToTrack(trackUi))
        val updatedHistory = getTracksHistory()
        historyLiveData.postValue(updatedHistory)
    }
    
    suspend fun addTrackToHistoryFromHistoryAdapter(trackUi: TrackUiModel) {
        addTrackToHistory(trackUi)
        searchStateLiveData.postValue(TrackSearchState.History)
    }
    
    suspend fun getTracksHistory(): List<TrackUiModel> {
        val tracks = searchHistoryInteractor.getTracksHistory()
        return TrackListUiMapper.trackListToTrackListUi(tracks)
    }
    
    fun onSearchFocused() {
        if (historyLiveData.value!!.isEmpty())
            searchStateLiveData.postValue(TrackSearchState.Default)
        else if (searchQueryLiveData.value!!.isEmpty() && historyLiveData.value!!.isNotEmpty())
            searchStateLiveData.postValue(TrackSearchState.History)
    }
    
    fun clearTracksHistory() {
        searchHistoryInteractor.clearTracksHistory()
        historyLiveData.postValue(emptyList())
        searchStateLiveData.postValue(TrackSearchState.Default)
    }
    
    fun updateSearchQuery(newQuery: String) {
        searchQueryLiveData.postValue(newQuery)
        
        if (newQuery.isEmpty() && historyLiveData.value!!.isNotEmpty())
            searchStateLiveData.postValue(TrackSearchState.History)
        else
            searchStateLiveData.postValue(TrackSearchState.Default)
        
        if (newQuery.isEmpty())
            clearButtonVisibleLiveData.postValue(false)
        else
            clearButtonVisibleLiveData.postValue(true)
    }
    
    fun search(query: String) {
        searchStateLiveData.postValue(TrackSearchState.Loading)
        
        viewModelScope.launch {
            tracksSearchInteractor
                .search(query)
                .collect { pair ->
                    responseProcessing(pair.first, pair.second)
                }
        }
    }
    
    private fun responseProcessing(foundTracks: List<Track>, resultCode: Int) {
        val trackListUi = TrackListUiMapper.trackListToTrackListUi(foundTracks)
        
        if (trackListUi.isEmpty() && resultCode == COMPLETE_CODE) {
            trackListLiveData.postValue(emptyList())
            searchStateLiveData.postValue(TrackSearchState.Empty)
            
        } else if (trackListUi.isNotEmpty() && resultCode == COMPLETE_CODE) {
            trackListLiveData.postValue(trackListUi)
            searchStateLiveData.postValue(TrackSearchState.Success)
            
        } else {
            searchStateLiveData.postValue(TrackSearchState.Error)
        }
    }
    
    companion object {
        private const val COMPLETE_CODE = 200
    }
}
