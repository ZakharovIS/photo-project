package com.example.unsplashpractice.domain

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashpractice.data.models.UnsplashPhotosDatabaseRepository
import com.example.unsplashpractice.db.Photo

class DBPagingSource (
    private val repository: UnsplashPhotosDatabaseRepository
) : PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: FIRST_PAGE

        return try {
            val entities = repository.getPagedList(params.loadSize, page * params.loadSize)
            Log.d("DB_PH", "${entities}")
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private companion object {
        private const val FIRST_PAGE = 0
    }

}