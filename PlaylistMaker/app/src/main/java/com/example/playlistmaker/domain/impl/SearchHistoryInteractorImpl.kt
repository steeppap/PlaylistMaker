package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryInteractorImpl(private val prefs: SharedPrefsRepository): SearchHistoryInteractor {
    companion object{
        private const val KEY_HISTORY = "search_history"
        private const val MAX_SIZE_HISTORY = 10
    }
    private val gson = Gson()

    override fun addTrackToHistory(track: Track) {
        val history: MutableList<Track> = getTracksHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_SIZE_HISTORY) {
            history.removeAt(MAX_SIZE_HISTORY)
        }

        prefs.saveSetting(KEY_HISTORY, gson.toJson(history))
    }

    override fun getTracksHistory(): List<Track> {
        val json = prefs.getSetting(KEY_HISTORY) as String?
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: listOf()
    }

    override fun clearTracksHistory() {
        prefs.clearSetting(KEY_HISTORY)
    }
}
