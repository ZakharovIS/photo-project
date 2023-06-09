package com.example.unsplashpractice.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashpractice.data.UnsplashCollectionsApiRepository
import com.example.unsplashpractice.data.models.UnsplashCollection

class CollectionsPagingSource (
    private val repository: UnsplashCollectionsApiRepository
) : PagingSource<Int, UnsplashCollection>() {

    override fun getRefreshKey(state: PagingState<Int, UnsplashCollection>): Int? = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashCollection> {
        val page = params.key ?: FIRST_PAGE

        return kotlin.runCatching {
            repository.getCollectionsListPaged(page)
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