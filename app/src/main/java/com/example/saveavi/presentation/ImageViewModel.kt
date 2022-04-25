package com.example.saveavi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saveavi.data.local.image.ImageDao
import com.example.saveavi.data.models.ImageEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import com.example.saveavi.core.Result
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ImageViewModel(private val dao : ImageDao):ViewModel() {
    fun fetchImages():StateFlow<Result<List<ImageEntity>>> = flow {
        kotlin.runCatching {
            dao.getImages()
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

    fun saveImage(imageEntity: ImageEntity):StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            dao.insertImage(imageEntity)
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

class ImageViewModelFactory(private val dao : ImageDao):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ImageDao::class.java).newInstance(dao)
    }

}