package com.example.playlistmaker.data.extension

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track


object TrackListMapper {
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
