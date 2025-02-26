package com.shubh.shubhflix.ui.activity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubh.shubhflix.data.apiresponse.Hit
import com.shubh.shubhflix.data.apiresponse.Medium
import com.shubh.shubhflix.helper.ApiState
import com.shubh.shubhflix.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val repository: VideoRepository) : ViewModel() {

    private val _apiState = MutableStateFlow<ApiState<List<Hit>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<List<Hit>>> = _apiState

    fun fetchVideos(query:String) {
        viewModelScope.launch {
            _apiState.value = ApiState.Loading


            try {
                val videos = repository.getVideos(query)
                _apiState.value = ApiState.Success(videos)
            } catch (e: Exception) {
                _apiState.value = ApiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
