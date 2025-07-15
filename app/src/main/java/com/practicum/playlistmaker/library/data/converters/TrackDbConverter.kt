package com.practicum.playlistmaker.library.data.converters

import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.track_search.domain.models.Track

class TrackDbConverter {

    fun mapTrackToEntity(track: Track): TrackEntity = TrackEntity(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.trackTime,
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl
    )

    fun mapEntityToTrack(entity: TrackEntity): Track = Track(
        trackId = entity.trackId,
        trackName = entity.trackName,
        artistName = entity.artistName,
        trackTime = entity.trackTime,
        artworkUrl100 = entity.artworkUrl100,
        collectionName = entity.collectionName,
        releaseDate = entity.releaseDate,
        primaryGenreName = entity.primaryGenreName,
        country = entity.country,
        previewUrl = entity.previewUrl
    )
}