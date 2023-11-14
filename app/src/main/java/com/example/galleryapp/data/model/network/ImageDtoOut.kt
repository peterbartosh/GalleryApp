package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName

data class ImageDtoOut(
    @SerializedName("id") val id: Int?,
    @SerializedName("url") val url: String?,
    @SerializedName("date") val date: Int?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?
): ApiResponseData
