package com.example.saveavi.data.local.video

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.example.saveavi.data.models.VideoEntity

@Dao
interface VideoDao {

    @Query("SELECT * FROM videoentity")
    suspend fun getVideos():List<VideoEntity>

    @Insert(onConflict = ABORT)
    suspend fun insertVideo(videoEntity: VideoEntity):Long

}