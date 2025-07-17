package com.practicum.playlistmaker.library.data.converters

import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.util.Utils

class PlaylistDbConverter {

    fun mapPlaylistToEntity(playlist: Playlist): PlaylistEntity = PlaylistEntity(
        id = playlist.id,
        name = playlist.name,
        description = playlist.description,
        coverPath = playlist.coverPath,
        tracksIds = Utils().serializeToJson(playlist.tracksIds),
        tracksCount = playlist.tracksCount
    )

    fun mapEntityToPlaylist(entity: PlaylistEntity): Playlist = Playlist(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        coverPath = entity.coverPath,
        tracksIds = Utils().createIntListFromJson(entity.tracksIds),
        tracksCount = entity.tracksCount
    )
}