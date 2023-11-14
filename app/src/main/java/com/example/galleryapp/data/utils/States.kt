package com.example.galleryapp.data.utils

sealed class UiState(){
    class Success() : UiState()
    class Failure(): UiState()
    class Loading(): UiState()
    class NotStarted(): UiState()
}