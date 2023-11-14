package com.example.galleryapp.presentation.features.main

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val mainRoute = "Main"

fun NavController.navigateToMain(navOptions: NavOptions? = null){
    this.navigate(route = mainRoute, navOptions = navOptions)
}

fun NavGraphBuilder.mainScreen(
    navigateToPhotoScreen: (Int) -> Unit
){
        composable(route = mainRoute){
            val mainViewModel = hiltViewModel<MainViewModel>()
            MainScreen(
                mainViewModel = mainViewModel,
                onPhotoClicked = navigateToPhotoScreen
            )
        }

}