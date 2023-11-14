package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName

data class SignUserOutDto(
    @SerializedName("userId") val userId: Int?,
    @SerializedName("login") val login: String?,
    @SerializedName("token") val token: String?
): ApiResponseData
