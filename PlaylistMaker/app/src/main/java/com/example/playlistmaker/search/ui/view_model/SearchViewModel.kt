package com.example.playlistmaker.search.ui.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
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
    private var currentSearchRunnable: Runnable? = null
    private var query: String = ""
    private val searchStateLiveData = MutableLiveData<TrackSearchState>(TrackSearchState.Default)
    fun observeSearchState(): LiveData<TrackSearchState> = searchStateLiveData
    private val trackListLiveData = MutableLiveData(emptyList<TrackUiModel>())
    fun observeTrackList(): LiveData<List<TrackUiModel>> = trackListLiveData
    private val historyLiveData = MutableLiveData(trackListHistory)
    fun observeHistory(): LiveData<List<TrackUiModel>> = historyLiveData
    
    val handler = Handler(Looper.getMainLooper())
    private fun searchRunnable(query: String): Runnable {
        return Runnable {
            searchStateLiveData.postValue(TrackSearchState.Loading)
            try {
                search(query)
            } catch (e: Exception) {
                searchStateLiveData.postValue(
                    TrackSearchState.Error
                )
            } finally {
                currentSearchRunnable = null
            }
        }
    }
    fun addTrackToHistory(trackUi: TrackUiModel) {
        searchHistoryInteractor.addTrackToHistory(TrackUiMapper.trackUiModelToTrack(trackUi))
        val updatedHistory = getTracksHistory()
        historyLiveData.postValue(updatedHistory)
    }
    fun addTrackToHistoryFromHistoryAdapter(trackUi: TrackUiModel){
        addTrackToHistory(trackUi)
        searchStateLiveData.postValue(TrackSearchState.History)
    }
    
    fun getTracksHistory():List<TrackUiModel> {
        return TrackListUiMapper.trackListToTrackListUi(searchHistoryInteractor.getTracksHistory())
    }
    fun onTextChanged(inputText: String) {
        if (inputText.isEmpty()) {
            searchStateLiveData.postValue(TrackSearchState.History)
        } else {
            searchStateLiveData.postValue(TrackSearchState.Default)
        }
        searchDebounce(inputText, SEARCH_DEBOUNCE_DELAY)
    }
    fun onSearchFocused() {
        searchStateLiveData.postValue(TrackSearchState.History)
    }
    
    fun clearTracksHistory() {
        searchHistoryInteractor.clearTracksHistory()
        historyLiveData.postValue(emptyList())
        searchStateLiveData.postValue(TrackSearchState.Default)
    }
    
    private fun search(query: String) {
        
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
                    searchStateLiveData.postValue(TrackSearchState.Error)}
            }
        })
    }
    
    fun searchUpdate(query: String){
        searchStateLiveData.postValue(TrackSearchState.Loading)
        search(query)
    }
    
    fun searchDebounce(query: String, debounceDelay: Long) {
        this.query = query
        currentSearchRunnable?.let { handler.removeCallbacks(it) }
        
        if (query.isNotBlank()) {
            currentSearchRunnable = searchRunnable(query)
            handler.postDelayed(currentSearchRunnable!!, debounceDelay)
        } else {
            trackListLiveData.postValue(emptyList())
            searchStateLiveData.postValue(TrackSearchState.History)
            currentSearchRunnable = null
        }
    }
    
    companion object {
        fun getFactory(
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksSearchInteractor = Creator.provideTracksSearchInteractor()
                val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(context)
                SearchViewModel(searchHistoryInteractor, tracksSearchInteractor)
            }
        }
        private const val COMPLETE_CODE = 200
        private const val SEARCH_DEBOUNCE_DELAY = 1500L
    }
}
