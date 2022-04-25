package com.example.saveavi.data.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val audio : String,
    val title:String="",
    val description:String="",
)