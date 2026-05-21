package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun addTrackToHistory(track: Track)
    fun getTracksHistory(): List<Track>
    fun clearTracksHistory()
}
