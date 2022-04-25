package com.example.saveavi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saveavi.core.Result
import com.example.saveavi.data.local.video.VideoDao
import com.example.saveavi.data.models.VideoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class VideoViewModel(private val dao: VideoDao):ViewModel() {
    fun fetchVideos():StateFlow<Result<List<VideoEntity>>> = flow {
        kotlin.runCatching {
            dao.getVideos()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading()
    )

    fun saveVideo(videoEntity: VideoEntity):StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            dao.insertVideo(videoEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading()
    )

}
class VideoViewModelFactory(private val dao:VideoDao):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(VideoDao::class.java).newInstance(dao)
    }

}