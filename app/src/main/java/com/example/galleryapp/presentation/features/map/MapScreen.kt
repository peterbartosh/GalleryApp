package com.example.galleryapp.presentation.features.map

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.presentation.components.Loading
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    mapViewModel: MapViewModel
) {

    val markers = mapViewModel.markers.collectAsState()
    val uiState = mapViewModel.uiState.collectAsState()

    val mapProperties = MapProperties(
        isMyLocationEnabled = true
        //location.latitude != 0.0 && location.longitude != 0.0,
    )

    val minsk = LatLng(53.893009, 27.567444)

    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(minsk, 10f)
    }

    if (uiState.value is UiState.Loading)
        Loading()
    else
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        ){
            markers.value.forEach { image ->
                Marker(
                    state = MarkerState(position = LatLng(image.lat, image.lng)),
                    title = "____",
                    snippet = image.date
                )
            }
        }
}

private suspend fun CameraPositionState.centerOnLocation(location: Location) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        LatLng(location.latitude, location.longitude),
        15f
    ),
)