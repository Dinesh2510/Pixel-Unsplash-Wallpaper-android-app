package com.android.wallapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.network.ApiService
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class PhotoPagingSource(private val service: ApiService) : PagingSource<Int, PhotoResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoResponse> {
        val page = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        return try {
            val photos = service.getPhotos(page, params.loadSize)
           // if (page != 0) delay(3000)
            LoadResult.Page(
                data = photos,
                prevKey = if (page == UNSPLASH_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoResponse>): Int? {
        return state.anchorPosition
    }
}