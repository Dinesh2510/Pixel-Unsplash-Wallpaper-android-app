package com.android.wallapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.wallapp.models.CategoryResponse
import com.android.wallapp.network.ApiService

import retrofit2.HttpException
import java.io.IOException

const val STARTING_PAGE_INDEX = 1

class TopicPagingSource(
    private val service: ApiService,
) : PagingSource<Int, CategoryResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CategoryResponse> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val photos = service.getTopics(page, params.loadSize)
            LoadResult.Page(
                data = photos,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CategoryResponse>): Int? {
        return state.anchorPosition
    }
}