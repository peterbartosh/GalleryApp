package com.example.galleryapp.presentation.app

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.model.network.ImageDtoIn
import com.example.galleryapp.data.model.network.ImageDtoOut
import com.example.galleryapp.data.repository.RestApiRepository
import com.example.galleryapp.data.repository.RoomRepository
import com.example.galleryapp.data.model.room.ImageEntity
import com.example.galleryapp.data.user.UserData
import com.example.galleryapp.data.utils.Constants
import com.example.galleryapp.data.utils.checkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val restApiRepository: RestApiRepository
): ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveImage(
        location: Location,
        base64Str: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {

            val date = try {
                (Date().time / 1000).toInt()
            } catch (e: Exception) {
                0
            }

            val dataOrException =
                restApiRepository.postImage(
                    token = mapOf(Constants.USER_KEY to UserData.token),
                    imageDtoIn = ImageDtoIn(
                        base64Image = base64Str,
                        date = date,
                        lat = location.latitude,
                        lng = location.longitude
                    )
                )

            checkResponse(
                dataOrException = dataOrException,
                onError = onResult,
                onSuccess = { data ->
                    launch {
                        addLocally(data)
                    }.invokeOnCompletion {
                        onResult("Added successfully!")
                    }
                }
            )
        }
    }

    private fun addLocally(data: ImageDtoOut) = viewModelScope.launch {

        //data.forEach { imageDtoOut ->
        data.id?.let { id ->
                roomRepository.addImage(
                    ImageEntity(
                        id = id,
                        userId = UserData.userId,
                        url = data.url,
                        date = data.date,
                        lat = data.lat,
                        lng = data.lng
                    )
                )
            }
        //}
    }
}