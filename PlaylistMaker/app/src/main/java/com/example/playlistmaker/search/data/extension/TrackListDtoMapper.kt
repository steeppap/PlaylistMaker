package com.example.playlistmaker.search.data.extension

import com.example.playlistmaker.search.data.dto.SearchHistoryItem
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track


object TrackListDtoMapper {
    fun trackListToSearchItemList(trackList: List<Track>): List<SearchHistoryItem> {
        return trackList.map { track ->
            TrackDtoMapper.trackToSearchHistoryItem(track)
        }
    }
    fun searchItemListToTrackList(searchItemList: List<SearchHistoryItem>): List<Track> {
        return searchItemList.map { searchHistoryItem ->
            TrackDtoMapper.searchHistoryItemToTrack(searchHistoryItem)
        }
    }

    fun domainToDataModel(trackList: List<Track>): List<TrackDto> {
        return trackList.map { track ->
            TrackDtoMapper.domainToDataModel(track)
        }
    }

    fun dataToDomainModel(trackListDto: List<TrackDto>): List<Track> {
        return trackListDto.map { trackDto ->
            TrackDtoMapper.dataToDomainModel(trackDto)
        }
    }
}
