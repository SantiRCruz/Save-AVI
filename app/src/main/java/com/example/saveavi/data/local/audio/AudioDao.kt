package com.example.saveavi.data.local.audio

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.example.saveavi.data.models.AudioEntity

@Dao
interface AudioDao {
    @Query("SELECT * FROM audioentity")
    suspend fun getAudios():List<AudioEntity>

    @Insert(onConflict = ABORT)
    suspend fun insertAudio(audioEntity: AudioEntity):Long
}