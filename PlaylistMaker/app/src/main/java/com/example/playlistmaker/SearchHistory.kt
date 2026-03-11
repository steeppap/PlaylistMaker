package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val prefs: SharedPreferences) {
    private val gson = Gson()
    companion object {
        private const val MAX_SIZE_HISTORY = 10
        private const val KEY_HISTORY = "search_history"
    }

    fun addTrack(track: Track) {
        val history: MutableList<Track> = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_SIZE_HISTORY) {
            history.removeAt(MAX_SIZE_HISTORY)
        }

        prefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply()
    }

    fun getHistory(): List<Track> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return listOf()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: listOf()
    }

    fun clear() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
