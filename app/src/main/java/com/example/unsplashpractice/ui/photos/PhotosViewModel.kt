package com.example.unsplashpractice.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.unsplashpractice.data.UnsplashPhotosApiRepository
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UnsplashPhotosDatabaseRepository
import com.example.unsplashpractice.db.Photo
import com.example.unsplashpractice.domain.DBPagingSource
import com.example.unsplashpractice.domain.PhotosPagingSource
import com.example.unsplashpractice.domain.SearchPhotosPagingSource
import com.example.unsplashpractice.utils.Converter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val unsplashPhotosApiRepository: UnsplashPhotosApiRepository,
    private val dbPhotosRepository: UnsplashPhotosDatabaseRepository,

    ) : ViewModel() {

    lateinit var searchPhotos: Flow<PagingData<PreviewPhoto>>
    lateinit var pagedPhotos: Flow<PagingData<PreviewPhoto>>
    lateinit var dbPagedPhotos: Flow<PagingData<Photo>>


    init {
        loadRandomPhotos()
    }

    suspend fun cachePhotos(previewPhoto: PreviewPhoto) {
        dbPhotosRepository.addPhotoToDB(Converter.convertUnsplashPhotoToDbPhoto(previewPhoto))
    }

    fun loadRandomPhotos() {
        pagedPhotos = Pager(
            config = PagingConfig(
                30
            ),
            pagingSourceFactory = { PhotosPagingSource(unsplashPhotosApiRepository) }
        ).flow.cachedIn(viewModelScope)

        dbPagedPhotos = Pager(
            config = PagingConfig(
                5
            ),
            pagingSourceFactory = { DBPagingSource(dbPhotosRepository) }
        ).flow.cachedIn(viewModelScope)


    }

    fun searchPhotos(query: String) {
        searchPhotos = Pager(
            config = PagingConfig(30),
            pagingSourceFactory = { SearchPhotosPagingSource(query, unsplashPhotosApiRepository) }
        ).flow.cachedIn(viewModelScope)
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