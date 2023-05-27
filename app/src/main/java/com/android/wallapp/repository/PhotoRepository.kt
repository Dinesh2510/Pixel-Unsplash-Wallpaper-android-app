package com.android.wallapp.repository
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.wallapp.models.CategoryResponse
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.network.ApiService
import com.android.wallapp.paging.PhotoPagingSource
import com.android.wallapp.paging.TopicPagingSource

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val apiService: ApiService) {

    fun getPhotos(): Flow<PagingData<PhotoResponse>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = 15,
                pageSize = 15,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(apiService) }
        ).flow

    fun getTopicPhotos(): Flow<PagingData<CategoryResponse>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = 15,
                pageSize = 15,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { TopicPagingSource(apiService) }
        ).flow

    //suspend fun getPhotoDetails(id: String) = apiService.getPhotoDetails(id)
}