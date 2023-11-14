package com.example.galleryapp.presentation.features.photo

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.galleryapp.data.utils.Constants

const val photoRoute = "Photo"

fun NavController.navigateToPhoto(imageId: Int, navOptions: NavOptions? = null){
    this.navigate(route = photoRoute, navOptions = navOptions)
    this.currentBackStackEntry?.savedStateHandle?.set(Constants.IMAGE_ID_KEY, imageId)
}

fun NavGraphBuilder.photoScreen(){
        composable(route = photoRoute){ backStackEntry ->
            val photoViewModel = hiltViewModel<PhotoViewModel>()

            val imageId = backStackEntry.savedStateHandle.get<Int>(Constants.IMAGE_ID_KEY) ?: 0

            PhotoScreen(
                photoViewModel = photoViewModel,
                imageId = imageId,
                //onBackPressed = onBackPressed
            )
        }

}

