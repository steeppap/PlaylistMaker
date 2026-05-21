package com.example.playlistmaker.search.data.extension

import com.example.playlistmaker.search.data.dto.SearchHistoryItem
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track


object TrackListMapper {
    fun trackListToSearchItemList(trackList: List<Track>): List<SearchHistoryItem> {
        return trackList.map { track ->
            TrackMapper.trackToSearchHistoryItem(track)
        }
    }
    fun searchItemListToTrackList(searchItemList: List<SearchHistoryItem>): List<Track> {
        return searchItemList.map { searchHistoryItem ->
            TrackMapper.searchHistoryItemToTrack(searchHistoryItem)
        }
    }

    fun domainToDataModel(trackList: List<Track>): List<TrackDto> {
        return trackList.map { track ->
            TrackMapper.domainToDataModel(track)
        }
    }

    fun dataToDomainModel(trackListDto: List<TrackDto>): List<Track> {
        return trackListDto.map { trackDto ->
            TrackMapper.dataToDomainModel(trackDto)
        }
    }
}
