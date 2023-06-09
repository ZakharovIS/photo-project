package com.example.unsplashpractice.ui.photodetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplashpractice.data.UnsplashPhotosApiRepository
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UnsplashPhoto
import com.example.unsplashpractice.data.models.UnsplashPhotosDatabaseRepository
import com.example.unsplashpractice.utils.Converter
import com.example.unsplashpractice.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val unsplashPhotosApiRepository: UnsplashPhotosApiRepository,
    private val dbPhotosRepository: UnsplashPhotosDatabaseRepository
) : ViewModel() {

    private val _photoDetail = MutableStateFlow<UnsplashPhoto?>(null)
    val photoDetail = _photoDetail.asStateFlow()

    private val _updatedPhoto = MutableStateFlow<PreviewPhoto?>(null)
    val updatedPhoto = _updatedPhoto.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()


    fun loadPhoto(id: String) {
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                _photoDetail.value = unsplashPhotosApiRepository.getPhoto(id)
                _state.value = State.Success
            } catch (exception: Exception) {
                _state.value = State.Error
                getPhotoFromDb(id)
                _state.value = State.Success
            }
            if (_photoDetail.value == null) {
                getPhotoFromDb(id)
                _state.value = State.Success
            }
            if (_photoDetail.value == null) {
                _state.value = State.Error
            }
        }
    }

    suspend fun getPhotoFromDb(id: String) {
        _photoDetail.value = Converter.convertDbPhotoToUnsplashPhoto(dbPhotosRepository.getPhoto(id))

    }

    fun setLike(id: String) {
        viewModelScope.launch {
            _updatedPhoto.value = unsplashPhotosApiRepository.setLikeToPhoto(id)
        }
    }

    fun removeLike(id: String) {
        viewModelScope.launch {
            _updatedPhoto.value = unsplashPhotosApiRepository.removeLikeFromPhoto(id)
        }
    }


}