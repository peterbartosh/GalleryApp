package com.example.galleryapp.presentation.features.photo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.galleryapp.data.utils.Constants
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.data.utils.dateTimeFormat
import com.example.galleryapp.data.utils.getHeightPercent
import com.example.galleryapp.data.utils.showToast
import com.example.galleryapp.presentation.components.Loading

@Composable
fun PhotoScreen(
    photoViewModel: PhotoViewModel,
    imageId: Int,
    //onBackPressed: () -> Unit
) {

    val context = LocalContext.current
    val hp = getHeightPercent(context = context)

    val uiState = photoViewModel.uiState.collectAsState()
    val comments = photoViewModel.comments.collectAsState()

    val inputState = remember {
        mutableStateOf("")
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = true){
        photoViewModel.loadImage(imageId).join()
        photoViewModel.loadData { message ->
            context.showToast(message)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (uiState.value){
            is UiState.Success -> {

                val imageEntity = photoViewModel.image

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(hp * 85)
                ) {


                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.45f),
                        //.padding(bottom = 5.dp),
                        contentScale = ContentScale.Crop,
                        model = imageEntity?.url,
                        contentDescription = null
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        imageEntity?.date?.let { seconds ->
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = dateTimeFormat(seconds)
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        state = lazyListState,
                        //contentPadding = PaddingValues(bottom = 5.dp, start = 3.dp, end = 3.dp)
                    ) {
                        itemsIndexed(items = comments.value, key = { _, comment ->
                            comment.id ?: comment.hashCode()
                        }) { ind, comment ->
                            if (ind == comments.value.lastIndex && (ind + 1) % Constants.ITEMS_PER_PAGE_AMOUNT == 0)
                                LaunchedEffect(key1 = true) {
                                    photoViewModel.fetchCommentsFromApi { message ->
                                        context.showToast(message)
                                    }
                                }

                            CommentCard(comment){ id ->
                                photoViewModel.removeComment(id)
                            }
                        }
                    }
                }

                CommentInputField(
                    height = hp * 12,
                    input = inputState,
                    onSendClick = { input ->
                        photoViewModel.addComment(
                            text = input,
                            onError = { message ->
                                context.showToast(message)
                            },
                            onSuccess = { lastIndex ->
                                lazyListState.scrollToItem(lastIndex)
                            }
                        )
                    }
                )

            }
            is UiState.Loading -> Loading()
            else -> Text(text = "Failure occured")
        }
    }

}
