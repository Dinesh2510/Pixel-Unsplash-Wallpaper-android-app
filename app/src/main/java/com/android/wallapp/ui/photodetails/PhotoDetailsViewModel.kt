package com.android.wallapp.ui.photodetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.models.User
import com.android.wallapp.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _photoDetails = MutableStateFlow<PhotoDetails>(PhotoDetails.Empty)
    val photoDetails: StateFlow<PhotoDetails> = _photoDetails

    private val photoDetailsEventChannel = Channel<PhotoDetailsEvent>(Channel.CONFLATED)
    val photoDetailsEvent = photoDetailsEventChannel.receiveAsFlow()


    fun onPhotoClick(photo: PhotoResponse) = viewModelScope.launch {
        photoDetailsEventChannel.send(PhotoDetailsEvent.NavigateToPhotoZoom(photo))
    }


    sealed class PhotoDetails {
        data class Success(val photoDetails: PhotoResponse) : PhotoDetails()
        object Empty : PhotoDetails()
    }

    sealed class PhotoDetailsEvent {
        data class NavigateToPhotoZoom(val photo: PhotoResponse) : PhotoDetailsEvent()
    }}