package com.example.galleryapp.data.model.doe

import com.example.galleryapp.data.model.network.ApiResponseData
import com.example.galleryapp.data.model.network.ResponseDto

data class DataOrException <T: ApiResponseData>(
    var data: ResponseDto<T>? = null,
    val exception: Exception? = null
)