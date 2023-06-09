package com.example.unsplashpractice.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.unsplashpractice.data.UnsplashPhotosApiRepository
import com.example.unsplashpractice.data.UserApiRepository
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UserPrivate
import com.example.unsplashpractice.domain.UserPhotosPagingSource
import com.example.unsplashpractice.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userApiRepository: UserApiRepository,
    private val unsplashPhotosApiRepository: UnsplashPhotosApiRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val _userData = MutableStateFlow<UserPrivate?>(null)
    val userData = _userData.asStateFlow()

    lateinit var pagedPhotos: Flow<PagingData<PreviewPhoto>>

    init {
        loadMyData()
    }

    fun loadMyFavoritePhotos(username: String) {
        pagedPhotos = Pager(
            config = PagingConfig(
                30
            ),
            pagingSourceFactory = { UserPhotosPagingSource(username, userApiRepository) }
        ).flow.cachedIn(viewModelScope)

    }

    fun loadMyData() {
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                _state.value = State.Success
                _userData.value = userApiRepository.getMyData()
                if (_userData.value == null) _state.value = State.Error

            } catch (exception: Exception) {
                _state.value = State.Error
            }

        }
    }

    fun setLike(id: String) {
        viewModelScope.launch {
            unsplashPhotosApiRepository.setLikeToPhoto(id)
        }

    }

    fun removeLike(id: String) {
        viewModelScope.launch {
            unsplashPhotosApiRepository.removeLikeFromPhoto(id)
        }
    }


}