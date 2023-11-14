package com.example.galleryapp.presentation.features.photo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.model.network.CommentDtoIn
import com.example.galleryapp.data.model.network.CommentDtoOut
import com.example.galleryapp.data.model.room.CommentEntity
import com.example.galleryapp.data.model.room.ImageEntity
import com.example.galleryapp.data.repository.RestApiRepository
import com.example.galleryapp.data.repository.RoomRepository
import com.example.galleryapp.data.user.UserData
import com.example.galleryapp.data.utils.Constants
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.data.utils.checkResponse
import com.example.galleryapp.presentation.app.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val restApiRepository: RestApiRepository
) : ViewModel() {

    var image: ImageEntity? = null
    private var page = 0

    private val _comments = MutableStateFlow(listOf<CommentDtoOut>())
    val comments: StateFlow<List<CommentDtoOut>> = _comments

    private val _uiState = MutableStateFlow<UiState>(UiState.NotStarted())
    val uiState: StateFlow<UiState> = _uiState

    fun loadData(onError: (String) -> Unit) = viewModelScope.launch {

        _uiState.value = UiState.Loading()

        image?.let { img ->
            roomRepository.getImageComments(img.id)
                .catch { e ->
                    Log.d(TAG, "init: $e")
                    _uiState.value = UiState.Failure()
                }
                .collect { coms ->
                    _comments.value = coms.map { commentEntity ->
                        commentEntity.toCommentDtoOut()
                    }
                    if (_comments.value.isEmpty())
                        fetchCommentsFromApi(onError = onError)
                     else if (_uiState.value !is UiState.Failure)
                        _uiState.value = UiState.Success()
                }
        }
    }

    fun loadImage(imageId: Int) = viewModelScope.launch {
        try {
            image = roomRepository.getImageById(imageId)
        } catch (e: Exception) {
            Log.d(TAG, "loadImage: $e")
        }
    }


    fun fetchCommentsFromApi(onError: (String) -> Unit) = viewModelScope.launch {

        page = try {
            (_comments.value.size.toDouble() / Constants.ITEMS_PER_PAGE_AMOUNT.toDouble()).toInt()
        } catch (e: Exception){
            0
        }

        image?.let { img ->
            val dataOrException = restApiRepository.getComments(
                token = mapOf(Constants.USER_KEY to UserData.token),
                imageId = img.id,
                page = page
            )
            _uiState.value = when (dataOrException.data?.status) {
                    200 -> {
                    try {
                        dataOrException.data?.data?.let { data ->
                            addLocally(data)
                        }
                        //page++
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
    }

    fun addComment(
        text: String,
        onError: (String) -> Unit,
        onSuccess: suspend (Int) -> Unit
    ) = viewModelScope.launch {
        image?.let { img ->

            val dataOrException = restApiRepository.postComment(
                token = mapOf(Constants.USER_KEY to UserData.token),
                commentDtoIn = CommentDtoIn(text),
                imageId = img.id
            )

            _uiState.value = checkResponse(
                dataOrException = dataOrException,
                onError = { message ->
                    Log.d(TAG, "addComment: $message")
                    onError(message)
                },
                onSuccess = {
                    dataOrException.data?.data?.let { data ->
                        addLocally(listOf(data)).join()
                        delay(100)
                        onSuccess(_comments.value.lastIndex)
                    }

            })
        }
    }

    fun removeComment(commentId: Int) = viewModelScope.launch {
        image?.let { img ->
            val dataOrException = restApiRepository.deleteComment(
                token = mapOf(Constants.USER_KEY to UserData.token),
                commentID = commentId,
                imageId = img.id
            )

            _uiState.value = checkResponse(
                onSuccessNecessary = {
                    removeLocally(commentId)
                },
                dataOrException = dataOrException,
                onError = {
                    Log.d(TAG, "removeComment: $it")
                },
                onSuccess = {}
            )
        }
    }

    private fun addLocally(data: List<CommentDtoOut>) = viewModelScope.launch {
        image?.id?.let { imgId ->
            roomRepository.addAllComments(data.mapNotNull { it.toCommentEntity(imgId) })
        }
    }

    private fun removeLocally(commentId: Int) = viewModelScope.launch {
        roomRepository.deleteComment(commentId)
    }


    private fun CommentDtoOut.toCommentEntity(imgId: Int) =
        this.id?.let { comId ->
            CommentEntity(
                id = comId,
                imageId = imgId,
                date = this.date,
                text = this.text
            )
        }

    private fun CommentEntity.toCommentDtoOut() =
        CommentDtoOut(
            id = this.id,
            date = this.date,
            text = this.text
        )


}

