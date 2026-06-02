package com.example.playlistmaker.search.ui.extension

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TrackUiModel


object TrackListUiMapper {
    fun trackListToTrackListUi(trackList: List<Track>): List<TrackUiModel> {
        return trackList.map { track ->
            TrackUiMapper.trackToTrackUiModel(track)
        }
    }
    fun trackListUiToTrackList(trackListUi: List<TrackUiModel>): List<Track> {
        return trackListUi.map { trackUiModel ->
            TrackUiMapper.trackUiModelToTrack(trackUiModel)
        }
    }
}
