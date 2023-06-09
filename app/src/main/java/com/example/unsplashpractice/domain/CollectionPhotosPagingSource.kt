package com.example.unsplashpractice.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashpractice.data.UnsplashCollectionsApiRepository
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UnsplashCollection

class CollectionPhotosPagingSource (
    private val collection_id: String,
    private val repository: UnsplashCollectionsApiRepository
) : PagingSource<Int, PreviewPhoto>() {

    override fun getRefreshKey(state: PagingState<Int, PreviewPhoto>): Int? = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PreviewPhoto> {
        val page = params.key ?: FIRST_PAGE

        return kotlin.runCatching {
            repository.getCollectionPhotos(page, collection_id)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }

}