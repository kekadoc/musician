package com.kekadoc.project.musician.ui.albums

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kekadoc.project.musician.core.network.api.NetworkException
import com.kekadoc.project.musician.feature.album.network.api.Album
import com.kekadoc.project.musician.feature.album.network.api.AlbumService

class AlbumPagingSource(private val service: AlbumService) : PagingSource<Int, Album>() {
    companion object {
        private const val TAG: String = "AlbumPagingSource-TAG"
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = service.load(nextPageNumber, params.loadSize)
            if (response.error.code != 0) throw NetworkException(response.error)
            LoadResult.Page(
                data = response.library.albums.map { it.value },
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Throwable) {
            Log.e(TAG, "load: $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}