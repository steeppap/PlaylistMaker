package com.example.playlistmaker.search.data.extension

import com.example.playlistmaker.search.data.dto.SearchHistoryItem
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track

object TrackMapper {
    fun trackToSearchHistoryItem(track: Track): SearchHistoryItem{
        return SearchHistoryItem(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
    fun searchHistoryItemToTrack(searchHistoryItem: SearchHistoryItem): Track{
        return Track(
            trackName = searchHistoryItem.trackName,
            artistName = searchHistoryItem.artistName,
            trackTimeMillis = searchHistoryItem.trackTimeMillis,
            artworkUrl100 = searchHistoryItem.artworkUrl100,
            trackId = searchHistoryItem.trackId,
            collectionName = searchHistoryItem.collectionName,
            releaseDate = searchHistoryItem.releaseDate,
            primaryGenreName = searchHistoryItem.primaryGenreName,
            country = searchHistoryItem.country,
            previewUrl = searchHistoryItem.previewUrl
        )
    }
    fun dataToDomainModel(trackDto: TrackDto): Track {
        return Track(
            trackName = trackDto.trackName,
            artistName = trackDto.artistName,
            trackTimeMillis = trackDto.trackTimeMillis,
            artworkUrl100 = trackDto.artworkUrl100,
            trackId = trackDto.trackId,
            collectionName = trackDto.collectionName,
            releaseDate = trackDto.releaseDate,
            primaryGenreName = trackDto.primaryGenreName,
            country = trackDto.country,
            previewUrl = trackDto.previewUrl
        )
    }


    fun domainToDataModel(track: Track): TrackDto {
        return TrackDto(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
