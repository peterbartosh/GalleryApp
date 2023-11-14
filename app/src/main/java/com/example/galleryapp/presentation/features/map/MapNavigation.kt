package com.example.galleryapp.presentation.features.map

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val mapRoute = "Map"

fun NavController.navigateToMap(navOptions: NavOptions? = null){
    this.navigate(route = mapRoute, navOptions = navOptions)
}

fun NavGraphBuilder.mapScreen(
//    navigateToMain: () -> Unit
){
        composable(route = mapRoute){
            val mapViewModel = hiltViewModel<MapViewModel>()
            MapScreen(mapViewModel = mapViewModel)
        }

}