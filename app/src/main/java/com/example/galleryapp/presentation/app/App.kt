package com.example.galleryapp.presentation.app

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.galleryapp.data.utils.showToast
import com.example.galleryapp.presentation.features.authentication.authRoute
import com.example.galleryapp.presentation.features.main.mainRoute
import com.example.galleryapp.presentation.features.map.mapRoute
import com.example.galleryapp.presentation.features.photo.photoRoute
import com.example.galleryapp.presentation.theme.GalleryAppTheme
import com.example.galleryapp.data.location.getCurrentLocation
import kotlinx.coroutines.launch

const val TAG = "ERROR_TAG"

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun App() {

    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    // Scaffold states
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var showBackIcon by remember{
        mutableStateOf(false)
    }
    var isAuthScreen by remember {
        mutableStateOf(true)
    }
    var showFAB by remember {
        mutableStateOf(true)
    }


    navController.addOnDestinationChangedListener{ _, dest, _ ->
        isAuthScreen = dest.route in listOf(authGraphRoute, authRoute)
        showBackIcon = dest.route in listOf(photoRoute)
        showFAB = dest.route in listOf(mainGraphRoute, mainRoute, mapRoute)
    }

    val appViewModel = hiltViewModel<AppViewModel>()

    GalleryAppTheme(darkTheme = false) {

        ModalNavigationDrawer(
            gesturesEnabled = !isAuthScreen,
            drawerState = drawerState,
            drawerContent = {
                NavigationSheet(snackbarHostState, navigateSavable = { route ->
                    navController.navigate(
                        route = route,
                        navOptions = navOptions {
                            navController.currentDestination?.id?.let { destId ->
                                popUpTo(destId) {
                                    //if (navController.currentDestination?.route == mapRoute)
                                    saveState = true
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }){
                    scope.launch {
                        drawerState.apply {
                            close()
                        }
                    }
                }
            },
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                        AppBar(
                            onMenuIconClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            },
                            onBackIconClick = { navController.popBackStack() },
                            showBackIcon = showBackIcon,
                            isAuthScreen = isAuthScreen
                        )
                },
                floatingActionButton = {
                    if (showFAB)
                        FAB(
                            snackbarHostState = snackbarHostState,
                            saveImageToDatabase = { base64 ->
                                getCurrentLocation(context){ location ->
                                    appViewModel.saveImage(
                                        location = location,
                                        base64Str = base64,
                                        onResult = { message ->
                                            Log.d(TAG, "App: $message")
                                            context.showToast(message)
                                        }
                                    )
                                }
                            }
                        )
                }
            ) { contentPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    color = MaterialTheme.colorScheme.background
                ) {

                    AppNavHost(navController)

                }
            }
        }
    }
}