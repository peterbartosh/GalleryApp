package com.example.galleryapp.data.model.doe

import com.example.galleryapp.data.model.network.ResponseDtoComment

data class DataOrExceptionComments(
    var data: ResponseDtoComment? = null,
    val exception: Exception? = null
)