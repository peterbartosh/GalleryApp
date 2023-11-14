package com.example.galleryapp.presentation.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.example.galleryapp.presentation.features.authentication.authRoute
import com.example.galleryapp.presentation.features.authentication.authScreen
import com.example.galleryapp.presentation.features.main.MainViewModel
import com.example.galleryapp.presentation.features.main.mainRoute
import com.example.galleryapp.presentation.features.main.mainScreen
import com.example.galleryapp.presentation.features.map.mapScreen
import com.example.galleryapp.presentation.features.photo.navigateToPhoto
import com.example.galleryapp.presentation.features.photo.photoScreen

const val authGraphRoute = "Auth Graph"
const val mainGraphRoute = "Main Graph"

fun NavController.navigateToMainGraph(){
    val navController = this
    navController.navigate(route = mainGraphRoute){
        navController.currentDestination?.id?.let { destId ->
            popUpTo(destId) {
                //saveState = true
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }

    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = authGraphRoute){
        navigation(startDestination = authRoute, route = authGraphRoute) {
            authScreen(navigateToMain = navController::navigateToMainGraph)
        }
        navigation(startDestination = mainRoute, route = mainGraphRoute) {
            mainScreen(navigateToPhotoScreen = navController::navigateToPhoto)
            mapScreen()
            photoScreen()
        }
    }
}