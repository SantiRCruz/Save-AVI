package com.example.saveavi.data.local.image

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.example.saveavi.data.models.ImageEntity

@Dao
interface ImageDao {
    @Query("SELECT * FROM imageentity")
     suspend fun getImages():List<ImageEntity>

    @Insert(onConflict = ABORT)
    suspend fun insertImage(imageEntity: ImageEntity):Long


}