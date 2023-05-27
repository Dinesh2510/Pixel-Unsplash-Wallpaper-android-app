package com.android.wallapp.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.wallapp.models.PhotoResponse

import com.android.wallapp.repository.PhotoRepository
import com.android.wallapp.repository.TopicRepository
import com.android.wallapp.utlis.Resource

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val photoRepository: PhotoRepository,
    private val state: SavedStateHandle,
) : ViewModel() {



    private val photosChannel = Channel<String?>(Channel.CONFLATED)

    val getAllPhotos = flowOf(photosChannel.receiveAsFlow().map { PagingData.empty() },
        state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY).asFlow().flatMapLatest { id ->
            photoRepository.getPhotos()
        }.cachedIn(viewModelScope)
    ).flattenMerge()



    companion object {
        private const val CURRENT_QUERY = "current_query"
        private var DEFAULT_QUERY = "0"
    }
}