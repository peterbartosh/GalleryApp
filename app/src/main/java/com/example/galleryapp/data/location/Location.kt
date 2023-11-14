package com.example.galleryapp.data.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun getCurrentLocation(
    context: Context,
    onSuccess: (Location) -> Unit
) {
    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
    ) {
        LocationServices.getFusedLocationProviderClient(context).getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            onSuccess(location)
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckAndRequest(
    snackbarHostState: SnackbarHostState,
    requestPermissions: MutableState<Boolean>,
    includeCameraPermission: Boolean = true,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val permissionsList = mutableListOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    if (includeCameraPermission) permissionsList.add(Manifest.permission.CAMERA)

    val permissions = rememberMultiplePermissionsState(
        permissions = permissionsList
    )

    LaunchedEffect(key1 = requestPermissions.value){
        if (requestPermissions.value) {
            if (!permissions.allPermissionsGranted || permissions.shouldShowRationale)
                requestPermissions(scope, snackbarHostState, permissions)

            if (!isGPSEnabled(context))
                requestToEnableGPS(context, snackbarHostState, scope)

            if (permissions.allPermissionsGranted &&
                !permissions.shouldShowRationale &&
                isGPSEnabled(context)
            ) onSuccess()

            requestPermissions.value = false
        }
    }
}


fun requestToEnableGPS(
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    scope.launch {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        val canNavigateToGPSSettings =
            intent.resolveActivity(context.packageManager) != null

        val result = snackbarHostState.showSnackbar(
            message = "GPS is disabled",
            actionLabel = if (!canNavigateToGPSSettings) {
                null
            } else {
                "ENABLE"
            },
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite,
        )

        when (result) {
            SnackbarResult.Dismissed -> {}

            SnackbarResult.ActionPerformed -> {
                if (canNavigateToGPSSettings) {
                    context.startActivity(intent)
                }
            }
        }
    }
}

fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

@OptIn(ExperimentalPermissionsApi::class)
fun requestPermissions(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    permissions: MultiplePermissionsState
) {
    scope.launch {
        val result = snackbarHostState.showSnackbar(
            "Missing required permissions",
            "Grant",
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite,
        )

        when (result) {
            SnackbarResult.Dismissed -> {}

            SnackbarResult.ActionPerformed -> {
                permissions.launchMultiplePermissionRequest()
            }
        }
    }

}