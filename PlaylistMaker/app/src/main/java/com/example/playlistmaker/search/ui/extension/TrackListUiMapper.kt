package com.example.playlistmaker.search.ui.extension

import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TrackUiModel


object TrackListUiMapper {
    fun trackListToTrackListUi(trackList: List<Track>): List<TrackUiModel> {
        return trackList.map { track ->
            TrackUiMapper.trackToTrackUiModel(track)
        }
    }
    
    fun tracksPlaylistToTrackListUi(tracksPlaylist: List<TrackPlaylist>): List<TrackUiModel> {
        return tracksPlaylist.map { tracksPlaylist ->
            TrackUiMapper.trackPlaylistToTrackUiModel(tracksPlaylist)
        }
    }
}
