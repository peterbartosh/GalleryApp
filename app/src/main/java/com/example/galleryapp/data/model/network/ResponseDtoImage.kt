package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName


data class ResponseDtoImage(
    @SerializedName("status") val status: Int?,
    @SerializedName("data") val data: List<ImageDtoOut>
)
