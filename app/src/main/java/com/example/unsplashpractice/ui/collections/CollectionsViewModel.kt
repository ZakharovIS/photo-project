package com.example.unsplashpractice.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.unsplashpractice.data.UnsplashCollectionsApiRepository
import com.example.unsplashpractice.data.UnsplashPhotosApiRepository
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UnsplashCollection
import com.example.unsplashpractice.domain.CollectionPhotosPagingSource
import com.example.unsplashpractice.domain.CollectionsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val unsplashCollectionsApiRepository: UnsplashCollectionsApiRepository,
    private val unsplashPhotosApiRepository: UnsplashPhotosApiRepository
) : ViewModel() {

    lateinit var pagedUnsplashCollection: Flow<PagingData<UnsplashCollection>>
    lateinit var pagedCollectionPhotos: Flow<PagingData<PreviewPhoto>>
    private val _collectionDetail = MutableStateFlow<UnsplashCollection?>(null)
    val collectionDetail = _collectionDetail.asStateFlow()



    init {
        loadRandomCollections()
    }

    fun loadRandomCollections() {
        pagedUnsplashCollection = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = { CollectionsPagingSource(unsplashCollectionsApiRepository) }
        ).flow.cachedIn(viewModelScope)
    }

    fun loadCollection(id: String) {
        viewModelScope.launch {
            _collectionDetail.value = unsplashCollectionsApiRepository.getCollection(id)
        }

    }

    fun loadCollectionPhotos(id: String) {
        pagedCollectionPhotos = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = { CollectionPhotosPagingSource(id, unsplashCollectionsApiRepository) }
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