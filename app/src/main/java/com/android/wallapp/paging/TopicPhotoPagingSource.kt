package com.android.wallapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.network.ApiService
import retrofit2.HttpException
import java.io.IOException

class TopicPhotoPagingSource(
    private val service: ApiService,
    private val topicId: String,
) : PagingSource<Int, PhotoResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoResponse> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val PhotoResponses =
                service.getTopicPhotos(topicId, page, params.loadSize)
            LoadResult.Page(
                data = PhotoResponses,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (PhotoResponses.isEmpty()) null else page + 1,
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