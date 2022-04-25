package com.example.saveavi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saveavi.core.Result
import com.example.saveavi.data.local.audio.AudioDao
import com.example.saveavi.data.models.AudioEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class AudioViewModel(private val dao : AudioDao):ViewModel() {

    fun fetchAudios():StateFlow<Result<List<AudioEntity>>> = flow {
        kotlin.runCatching {
            dao.getAudios()
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

    fun saveAudio(audioEntity: AudioEntity):StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            dao.insertAudio(audioEntity)
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
class AudioViewModelFactory(private val dao : AudioDao):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AudioDao::class.java).newInstance(dao)
    }
}