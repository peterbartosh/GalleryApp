package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName

data class ImageDtoIn(
    @SerializedName("base64Image") val base64Image: String?,
    @SerializedName("date") val date: Int?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?
)
