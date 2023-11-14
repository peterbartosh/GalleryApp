package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName

data class ResponseDto<T : ApiResponseData>(
    @SerializedName("status") val status: Int?,
    @SerializedName("data") val data: T
)