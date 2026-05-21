package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository):
    SearchHistoryInteractor {
    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun getTracksHistory(): List<Track> {
        return repository.getTracksHistory()
    }

    override fun clearTracksHistory() {
        repository.clearTracksHistory()
    }
}
