package com.android.wallapp.ui.topics

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.wallapp.models.CategoryResponse
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val state: SavedStateHandle,
) : ViewModel() {

    fun topicWisePics(id: String): Flow<PagingData<PhotoResponse>> = topicRepository.getTopicPhotosData(id)

    val getTopicsList: Flow<PagingData<CategoryResponse>> =
        topicRepository.getTopicData().cachedIn(viewModelScope)
}