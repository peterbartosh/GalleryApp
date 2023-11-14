package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName

data class CommentDtoOut(
    @SerializedName("id") val id: Int?,
    @SerializedName("date") val date: Int?,
    @SerializedName("text") val text: String?
): ApiResponseData
