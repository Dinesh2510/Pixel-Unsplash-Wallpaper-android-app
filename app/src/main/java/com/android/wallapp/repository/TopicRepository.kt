package com.android.wallapp.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.wallapp.models.CategoryResponse
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.network.ApiService
import com.android.wallapp.paging.TopicPagingSource
import com.android.wallapp.paging.TopicPhotoPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TopicRepository @Inject constructor(
    private val topicService: ApiService,
    private val application: Application,
) {

    fun getTopicData(): Flow<PagingData<CategoryResponse>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = 50,
                pageSize = 25,
                maxSize = 100,
                prefetchDistance = 25,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { TopicPagingSource(topicService) }
        ).flow
    fun getTopicPhotosData(topicId: String): Flow<PagingData<PhotoResponse>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = 15,
                pageSize = 15,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { TopicPhotoPagingSource(topicService, topicId) }
        ).flow


}
