package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName

data class ResponseDtoComment(
    @SerializedName("status") val status: Int?,
    @SerializedName("data") val data: List<CommentDtoOut>
)
