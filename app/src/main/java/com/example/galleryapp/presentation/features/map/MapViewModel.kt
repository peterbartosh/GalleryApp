package com.example.galleryapp.presentation.features.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.model.map.Marker
import com.example.galleryapp.data.model.network.ImageDtoOut
import com.example.galleryapp.data.model.room.ImageEntity
import com.example.galleryapp.data.repository.RoomRepository
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.data.utils.dateFormat
import com.example.galleryapp.presentation.app.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val roomRepository: RoomRepository) : ViewModel(){

    private val _markers = MutableStateFlow(listOf<Marker>())
    val markers : StateFlow<List<Marker>> = _markers

    private val _uiState = MutableStateFlow<UiState>(UiState.NotStarted())
    val uiState : StateFlow<UiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        _uiState.value = UiState.Loading()

        roomRepository.getAllImages()
            .catch { e ->
                Log.d(TAG, "loadImages: $e")
                _uiState.value = UiState.Loading()
            }
            .collect { imgs ->
                _markers.value = imgs.mapNotNull { img -> img.toMarker() }
                _uiState.value = UiState.Success()
            }
    }

    private fun ImageEntity.toMarker() =
        this.lat?.let { lat ->
            this.lng?.let { lng ->
                this.date?.let { seconds ->
                    Marker(
                        lat = lat,
                        lng = lng,
                        date = dateFormat(seconds)
                    )
                }
            }
        }

}