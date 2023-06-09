package com.example.unsplashpractice.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashpractice.data.UserApiRepository
import com.example.unsplashpractice.data.models.PreviewPhoto

class UserPhotosPagingSource (
    private val username: String,
    private val repository: UserApiRepository
) : PagingSource<Int, PreviewPhoto>() {

    override fun getRefreshKey(state: PagingState<Int, PreviewPhoto>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PreviewPhoto> {
        val page = params.key ?: FIRST_PAGE

        return kotlin.runCatching {
            repository.getPhotoLikedByMePaged(username, page)
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