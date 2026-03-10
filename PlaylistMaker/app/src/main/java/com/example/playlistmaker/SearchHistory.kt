package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val prefs: SharedPreferences) {
    private val gson = Gson()
    companion object {
        const val MAX_SIZE_HISTORY = 10
        const val KEY_HISTORY = "search_history"
    }

    fun addTrack(track: Track) {
        val history: ArrayList<Track> = getHistory()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_SIZE_HISTORY) {
            history.removeAt(MAX_SIZE_HISTORY)
        }

        prefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply()
    }

    fun getHistory(): ArrayList<Track> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return arrayListOf()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type) ?: arrayListOf()
    }

    fun clear() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
