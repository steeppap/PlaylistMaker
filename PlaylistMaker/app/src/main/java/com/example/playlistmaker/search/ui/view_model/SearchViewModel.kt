package com.example.playlistmaker.search.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksSearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.extension.TrackListUiMapper
import com.example.playlistmaker.search.ui.extension.TrackUiMapper
import com.example.playlistmaker.search.ui.models.TrackUiModel

class SearchViewModel(
    val searchHistoryInteractor: SearchHistoryInteractor,
    val tracksSearchInteractor: TracksSearchInteractor
) : ViewModel() {
    private val trackListHistory: List<TrackUiModel> = getTracksHistory()
    private val searchStateLiveData = MutableLiveData<TrackSearchState>(TrackSearchState.Default)
    fun observeSearchState(): LiveData<TrackSearchState> = searchStateLiveData
    private val trackListLiveData = MutableLiveData(emptyList<TrackUiModel>())
    fun observeTrackList(): LiveData<List<TrackUiModel>> = trackListLiveData
    private val historyLiveData = MutableLiveData(trackListHistory)
    fun observeHistory(): LiveData<List<TrackUiModel>> = historyLiveData
    private val searchQueryLiveData = MutableLiveData("")
    fun observeSearchQuery(): LiveData<String> = searchQueryLiveData
    private val clearButtonVisibleLiveData = MutableLiveData(false)
    fun observeClearButtonVisible(): LiveData<Boolean> = clearButtonVisibleLiveData
    
    fun addTrackToHistory(trackUi: TrackUiModel) {
        searchHistoryInteractor.addTrackToHistory(TrackUiMapper.trackUiModelToTrack(trackUi))
        val updatedHistory = getTracksHistory()
        historyLiveData.postValue(updatedHistory)
    }
    
    fun addTrackToHistoryFromHistoryAdapter(trackUi: TrackUiModel) {
        addTrackToHistory(trackUi)
        searchStateLiveData.postValue(TrackSearchState.History)
    }
    
    fun getTracksHistory(): List<TrackUiModel> {
        return TrackListUiMapper.trackListToTrackListUi(searchHistoryInteractor.getTracksHistory())
    }
    
    fun onSearchFocused() {
        if (historyLiveData.value!!.isEmpty()) searchStateLiveData.postValue(TrackSearchState.Default) else
            searchStateLiveData.postValue(TrackSearchState.History)
    }
    
    fun clearTracksHistory() {
        searchHistoryInteractor.clearTracksHistory()
        historyLiveData.postValue(emptyList())
        searchStateLiveData.postValue(TrackSearchState.Default)
    }
    
    fun updateSearchQuery(newQuery: String) {
        searchQueryLiveData.postValue(newQuery)
        
        if (newQuery.isEmpty() && historyLiveData.value!!.isNotEmpty()) searchStateLiveData.postValue(
            TrackSearchState.History
        ) else searchStateLiveData.postValue(TrackSearchState.Default)
        
        if (newQuery.isEmpty()) clearButtonVisibleLiveData.postValue(false) else clearButtonVisibleLiveData.postValue(
            true
        )
    }
    
    fun search(query: String) {
        searchStateLiveData.postValue(TrackSearchState.Loading)
        
        tracksSearchInteractor.search(query, object : TracksSearchInteractor.TracksConsumer {
            override fun consume(foundTracks: Pair<List<Track>, Int>) {
                val trackList = TrackListUiMapper.trackListToTrackListUi(foundTracks.first)
                val resultCode = foundTracks.second
                
                if (trackList.isEmpty() && resultCode == COMPLETE_CODE) {
                    trackListLiveData.postValue(emptyList())
                    searchStateLiveData.postValue(TrackSearchState.Empty)
                    
                } else if (trackList.isNotEmpty() && resultCode == COMPLETE_CODE) {
                    trackListLiveData.postValue(trackList)
                    searchStateLiveData.postValue(TrackSearchState.Success)
                    
                } else {
                    searchStateLiveData.postValue(TrackSearchState.Error)
                }
            }
        })
    }
    
    companion object {
        fun getFactory(
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(context)
                val tracksSearchInteractor = Creator.provideTracksSearchInteractor()
                SearchViewModel(searchHistoryInteractor, tracksSearchInteractor)
            }
        }
        
        private const val COMPLETE_CODE = 200
    }
}
