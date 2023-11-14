package com.example.galleryapp.data.repository

import android.util.Log
import com.example.galleryapp.data.model.doe.DataOrException
import com.example.galleryapp.data.model.doe.DataOrExceptionComments
import com.example.galleryapp.data.model.doe.DataOrExceptionImages
import com.example.galleryapp.data.model.network.ApiResponseData
import com.example.galleryapp.data.model.network.CommentDtoIn
import com.example.galleryapp.data.model.network.ImageDtoIn
import com.example.galleryapp.data.model.network.ResponseDto
import com.example.galleryapp.data.model.network.SignUserDtoIn
import com.example.galleryapp.data.network.RestApi
import com.example.galleryapp.presentation.app.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RestApiRepository @Inject constructor(private val restApi: RestApi) {

    private suspend fun <T: ApiResponseData> safeInvoke(func: suspend () -> ResponseDto<T>) =
        withContext(Dispatchers.IO) {
            try {
                val response = func.invoke()
                DataOrException(data = response)
            }
            catch (e: Exception) {
                Log.d(TAG, "safeInvoke: $e ${e.message}")
                DataOrException(exception = e)
            }
        }


    // users
    suspend fun signIn(userDtoIn: SignUserDtoIn) = safeInvoke{ restApi.signIn(userDtoIn) }

    suspend fun signUp(userDtoIn: SignUserDtoIn) = safeInvoke { restApi.signUp(userDtoIn) }


    // images
    suspend fun getImages(token: Map<String, String>, page: Int) =
        withContext(Dispatchers.IO) {
            try {
                val response = restApi.getImages(token, page)
                DataOrExceptionImages(data = response)
            } catch (e: Exception) {
                Log.d(TAG, "safeInvoke: $e")
                DataOrExceptionImages(exception = e)
            }
        }

    suspend fun postImage(token: Map<String, String>, imageDtoIn: ImageDtoIn) =
        safeInvoke { restApi.postImage(token, imageDtoIn) }

    suspend fun deleteImage(token: Map<String, String>, imageId: Int) =
        safeInvoke { restApi.deleteImage(token, imageId) }

    // comments
    suspend fun getComments(
        token: Map<String, String>,
        imageId: Int,
        page: Int
    ) = withContext(Dispatchers.IO) {
        try {
            val response = restApi.getComments(token, imageId, page)
            DataOrExceptionComments(data = response)
        } catch (e: Exception) {
            Log.d(TAG, "safeInvoke: $e ${e.message}")
            DataOrExceptionComments(exception = e)
        }
    }


    suspend fun postComment(
        token: Map<String, String>,
        commentDtoIn: CommentDtoIn,
        imageId: Int
    ) = safeInvoke { restApi.postComment(token, commentDtoIn, imageId) }

    suspend fun deleteComment(
        token: Map<String, String>,
        commentID: Int,
        imageId: Int
    ) = safeInvoke { restApi.deleteComment(token, commentID, imageId) }
}



