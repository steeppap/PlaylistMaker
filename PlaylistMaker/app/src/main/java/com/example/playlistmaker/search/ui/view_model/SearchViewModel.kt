package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksSearchInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
    val searchHistoryInteractor: SearchHistoryInteractor,
    val tracksSearchInteractor: TracksSearchInteractor
) : ViewModel() {
    private var currentSearchRunnable: Runnable? = null
    private var query: String = ""
    private val searchStateLiveData = MutableLiveData<TrackSearchState>(TrackSearchState.Default)
    fun observeSearchState(): LiveData<TrackSearchState> = searchStateLiveData
    private val trackListLiveData = MutableLiveData(emptyList<Track>())
    fun observeTrackList(): LiveData<List<Track>> = trackListLiveData
    
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
    
    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
    }
    
    fun getTracksHistory(): List<Track> {
        return searchHistoryInteractor.getTracksHistory()
    }
    
    fun clearTracksHistory() {
        searchHistoryInteractor.clearTracksHistory()
    }
    
    private fun search(query: String) {
        
        tracksSearchInteractor.search(query, object : TracksSearchInteractor.TracksConsumer {
            override fun consume(foundTracks: Pair<List<Track>, Int>) {
                val trackList = foundTracks.first
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
            searchHistoryInteractor: SearchHistoryInteractor,
            tracksSearchInteractor: TracksSearchInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(searchHistoryInteractor, tracksSearchInteractor)
            }
        }
        
        private const val COMPLETE_CODE = 200
    }
}
