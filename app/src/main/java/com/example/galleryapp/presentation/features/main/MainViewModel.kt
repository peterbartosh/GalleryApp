package com.example.galleryapp.presentation.features.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.model.network.ImageDtoOut
import com.example.galleryapp.data.repository.RestApiRepository
import com.example.galleryapp.data.repository.RoomRepository
import com.example.galleryapp.data.model.room.ImageEntity
import com.example.galleryapp.data.user.UserData
import com.example.galleryapp.data.utils.Constants
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.data.utils.checkResponse
import com.example.galleryapp.presentation.app.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val restApiRepository: RestApiRepository,
    private val roomRepository: RoomRepository
) : ViewModel(){

    private var page = 0

    private val _images = MutableStateFlow(listOf<ImageDtoOut>())
    val images : StateFlow<List<ImageDtoOut>> = _images

    private val _uiState = MutableStateFlow<UiState>(UiState.NotStarted())
    val uiState : StateFlow<UiState> = _uiState

    override fun onCleared() {
        Log.d("MAIN_VM_TAG", "onCleared: done")
        super.onCleared()
    }

    fun loadData(onError: (String) -> Unit) = viewModelScope.launch {
        _uiState.value = UiState.Loading()

        roomRepository.getAllImages()
            .catch { e ->
                Log.d(TAG, "loadImages: $e")
            }
            .collect { imgs ->
                //_uiState.value = UiState.Loading()
                _images.value =
                    imgs.map { img -> img.toImageDtoOut() }
                if (_images.value.isEmpty())
                    fetchImagesFromApi(onError = onError)
                else if (_uiState.value !is UiState.Failure)
                    _uiState.value = UiState.Success()
            }
    }


    suspend fun fetchImagesFromApi(onError: (String) -> Unit) {

        page = try {
            (_images.value.size.toDouble() / Constants.ITEMS_PER_PAGE_AMOUNT.toDouble()).toInt()
        } catch (e: Exception){
            0
        }

        val dataOrException = restApiRepository.getImages(
            token = mapOf(Constants.USER_KEY to UserData.token),
            page = page
        )

        _uiState.value = when (dataOrException.data?.status) {
            200 -> {
                try {
                    dataOrException.data?.data?.let { data ->
                        addLocally(data)
                    }
                    UiState.Success()
                } catch (e: Exception) {
                    val message = Constants.NOT_FOUND_MESSAGE
                    onError(message)
                    UiState.Failure()
                }
            }

            else -> {
                val message = try {
                    throw dataOrException.exception ?: Exception(Constants.NULL_MESSAGE)
                } catch (ioE: IOException) {
                    Constants.CONNECTION_LOST_MESSAGE
                } catch (hE: HttpException) {
                    hE.response()?.errorBody()?.string() ?: Constants.NULL_MESSAGE    // todo consider all statuses
                } catch (e: Exception) {
                    e.message.toString()
                }
                onError(message)
                UiState.Failure()
            }
        }
    }

    fun removeImage(imageId: Int) = viewModelScope.launch {
        _uiState.value = UiState.Loading()

        roomRepository.getImageCommentsAsList(imageId)
            .map { comment ->
                async {
                    val doe = async {
                        restApiRepository.deleteComment(
                            token = mapOf(Constants.USER_KEY to UserData.token),
                            commentID = comment.id,
                            imageId = imageId
                        )
                    }.await()

                    if (doe.exception == null) {
                        roomRepository.deleteComment(commentId = comment.id)
                    }
                }
            }.awaitAll()

        val dataOrException = restApiRepository.deleteImage(
            token = mapOf(Constants.USER_KEY to UserData.token),
            imageId = imageId
        )

        _uiState.value = checkResponse(
            onSuccessNecessary = {
                removeLocally(imageId)
            },
            dataOrException = dataOrException,
            onError = {
                Log.d(TAG, "removeComment: $it")
                removeLocally(imageId)
            },
            onSuccess = {}
        )
    }

    private fun addLocally(data: List<ImageDtoOut>) = viewModelScope.launch {
        roomRepository.addAllImages(data.mapNotNull { it.toImageEntity() })
    }

    fun removeLocally(imageId: Int) = viewModelScope.launch {
        roomRepository.deleteImage(imageId)
    }


    private fun ImageEntity.toImageDtoOut() = ImageDtoOut(
        id = this.id,
        url = this.url,
        date = this.date,
        lat = this.lat,
        lng = this.lng
    )

    private fun ImageDtoOut.toImageEntity() = this.id?.let {
        ImageEntity(
            id = it,
            userId = UserData.userId,
            url = this.url,
            date = this.date,
            lat = this.lat,
            lng = this.lng
        )
    }
}