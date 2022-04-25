package com.example.saveavi.data.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val video : String,
    val title:String="",
    val description:String="",
)