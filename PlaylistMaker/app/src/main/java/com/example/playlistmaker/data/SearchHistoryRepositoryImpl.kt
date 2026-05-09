package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.removeAll
import androidx.core.content.edit
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.extension.TrackListMapper
import com.example.playlistmaker.data.extension.TrackMapper
import com.example.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun addTrackToHistory(track: Track) {
        val trackDto = TrackMapper.domainToDataModel(track)
        val history: List<TrackDto> = TrackListMapper.domainToDataModel(getTracksHistory())
        val historyDto = history.toMutableList()

        historyDto.removeAll { it.trackId == track.trackId }
        historyDto.add(0, trackDto)

        if (historyDto.size > MAX_SIZE_HISTORY) {
            historyDto.removeAt(MAX_SIZE_HISTORY)
        }
        sharedPrefs.edit {
            putString(KEY_HISTORY, gson.toJson(historyDto))
        }
    }

    override fun getTracksHistory(): List<Track> {
        val json = sharedPrefs.getString(KEY_HISTORY, null)
        val type = object : TypeToken<List<TrackDto>>() {}.type
        val trackListDto: List<TrackDto> = gson.fromJson(json, type) ?: listOf()

        return TrackListMapper.dataToDomainModel(trackListDto)
    }

    override fun clearTracksHistory() {
        sharedPrefs.edit { remove(KEY_HISTORY) }
    }

    companion object {
        private const val KEY_HISTORY = "search_history"
        private const val MAX_SIZE_HISTORY = 10
    }
}
