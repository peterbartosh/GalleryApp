package com.example.galleryapp.data.model.network

import com.google.gson.annotations.SerializedName

//import com.google.gson.annotations.SerializedName


data class SignUserDtoIn (
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String
)