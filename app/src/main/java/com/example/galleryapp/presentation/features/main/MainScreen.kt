package com.example.galleryapp.presentation.features.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.galleryapp.data.utils.Constants
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.data.utils.showToast
import com.example.galleryapp.presentation.components.Error
import com.example.galleryapp.presentation.components.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onPhotoClicked: (Int) -> Unit
) {

    val context = LocalContext.current

    val uiState = mainViewModel.uiState.collectAsState()

    val images = mainViewModel.images.collectAsState()

    LaunchedEffect(key1 = true){
        mainViewModel.loadData { message ->
            context.showToast(message)
        }
    }

    when (uiState.value){
        is UiState.Success -> {
            if (images.value.isNotEmpty())
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    itemsIndexed(
                        items = images.value,
                        key = { _, image ->
                            image.id?.toString() ?: image.hashCode()
                        }
                    ) { ind, image ->

                        if (ind == images.value.lastIndex && (ind + 1) % Constants.ITEMS_PER_PAGE_AMOUNT == 0)
                            LaunchedEffect(key1 = true) {
                                mainViewModel.fetchImagesFromApi { message ->
                                    context.showToast(message)
                                }
                            }

                        ImageBox(
                            image = image,
                            onPhotoClicked = onPhotoClicked,
                            onDialogConfirmed = { id ->
                                mainViewModel.removeImage(id)
                            }

                        )
                    }
                }
            else
                Error(text = "Nothing found")
        }

        is UiState.Failure -> Error(text = "Error occurred")
        is UiState.Loading -> Column(modifier = Modifier.fillMaxSize()) {
            Loading()
        }
        else -> Box{}
    }
}