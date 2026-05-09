package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.removeAll
import androidx.core.content.edit
import com.example.playlistmaker.data.dto.SearchHistoryItem
import com.example.playlistmaker.data.extension.TrackListMapper
import com.example.playlistmaker.data.extension.TrackMapper
import com.example.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun addTrackToHistory(track: Track) {
        val historyItem = TrackMapper.trackToSearchHistoryItem(track)
        val history: MutableList<SearchHistoryItem> = TrackListMapper.trackListToSearchItemList(getTracksHistory()).toMutableList()

        history.removeAll { it.trackId == track.trackId }
        history.add(0, historyItem)

        if (history.size > MAX_SIZE_HISTORY) {
            history.removeAt(MAX_SIZE_HISTORY)
        }
        sharedPrefs.edit {
            putString(KEY_HISTORY, gson.toJson(history))
        }
    }

    override fun getTracksHistory(): List<Track> {
        val json = sharedPrefs.getString(KEY_HISTORY, null)
        val type = object : TypeToken<List<SearchHistoryItem>>() {}.type
        val searchItemList: List<SearchHistoryItem> = gson.fromJson(json, type) ?: listOf()

        return TrackListMapper.searchItemListToTrackList(searchItemList)
    }

    override fun clearTracksHistory() {
        sharedPrefs.edit { remove(KEY_HISTORY) }
    }

    companion object {
        private const val KEY_HISTORY = "search_history"
        private const val MAX_SIZE_HISTORY = 10
    }
}
