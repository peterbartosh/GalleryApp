package com.example.galleryapp.data.model.doe

import com.example.galleryapp.data.model.network.ResponseDtoImage

data class DataOrExceptionImages(
    var data: ResponseDtoImage? = null,
    val exception: Exception? = null
)