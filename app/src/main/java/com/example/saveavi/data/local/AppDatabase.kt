package com.example.saveavi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.saveavi.core.Converters
import com.example.saveavi.data.local.audio.AudioDao
import com.example.saveavi.data.local.image.ImageDao
import com.example.saveavi.data.local.video.VideoDao
import com.example.saveavi.data.models.AudioEntity
import com.example.saveavi.data.models.ImageEntity
import com.example.saveavi.data.models.VideoEntity

@Database(entities = [ImageEntity::class,VideoEntity::class,AudioEntity::class],version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase:RoomDatabase() {

    abstract fun ImageDao():ImageDao
    abstract fun VideoDao():VideoDao
    abstract fun AudioDao():AudioDao

    companion object{
        private var INSTANCE : AppDatabase ?= null

        fun getImageDatabase(context: Context):AppDatabase{
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "image_table"
            ).build()
            return INSTANCE!!
        }

        fun getVideoDatabase(context: Context):AppDatabase{
            INSTANCE = INSTANCE ?:Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "video_table"
            ).build()
            return INSTANCE!!
        }

        fun getAudioDatabase(context: Context):AppDatabase{
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "audio_table"
            ).build()
            return INSTANCE!!
        }

    }
}