package com.example.galleryapp.presentation.features.authentication

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val authRoute = "Auth"

fun NavController.navigateToAuth(navOptions: NavOptions? = null){
    this.navigate(route = authRoute, navOptions = navOptions)
}

fun NavGraphBuilder.authScreen(
    navigateToMain: () -> Unit
){
    composable(route = authRoute){
        val authViewModel = hiltViewModel<AuthViewModel>()
        AuthScreen(
            authViewModel = authViewModel,
            onAuthorisationDone = navigateToMain
        )
    }
}