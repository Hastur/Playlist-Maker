package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.TrackToPlaylistEntity

@Dao
interface TrackToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(track: TrackToPlaylistEntity)

    @Query("SELECT * from track_to_playlist_table where trackId in (:tracksIds)")
    suspend fun getTracksByIds(tracksIds: List<Int>): List<TrackToPlaylistEntity>

}